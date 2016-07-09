package info.iconmaster.who2comm.gui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import info.iconmaster.who2comm.Settings;
import info.iconmaster.who2comm.Utils;
import info.iconmaster.who2comm.user.ResultReason;
import info.iconmaster.who2comm.user.ResultReason.ReasonKind;
import info.iconmaster.who2comm.user.User;
import info.iconmaster.who2comm.user.User.Status;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Who2CommGUI extends Application {

	public static class UserListCell extends ListCell<User> {
		
		@Override
		public void updateItem(User item, boolean empty) {
			super.updateItem(item, empty);
			if (empty) {
				setText(null);
			} else {
				if (item.status == null) {
					setText(item.name);
				} else {
					setText(item.name + " : " + item.status);
					setTextFill(getColor(item.status));
				}
			}
		}
	}
	
	public static class UserNameTableCell extends TableCell<User,User> {
		@Override
		protected void updateItem(User item, boolean empty) {
			super.updateItem(item, empty);
			
			if (empty) {
				setText(null);
				setGraphic(null);
			} else {
				setText(item.name);
			}
		}
	}

	public static Paint getColor(Status status) {
		switch (status) {
			case CLOSED:
				return Color.RED;
			case INVALID:
				return Color.GOLD;
			case OPEN:
				return Color.GREEN;
			case UNKNOWN:
				return Color.GOLD;
			default:
				return null;
		}
	}

	public static Paint getColor(ReasonKind kind) {
		switch (kind) {
			case INVALID:
				return Color.GOLD;
			case NEGATIVE:
				return Color.RED;
			case POSITIVE:
				return Color.GREEN;
			case UNKNOWN:
				return Color.GOLD;
			default:
				return null;
		}
	}

	public static void showDetailWindow(User u) {
		ImageView icon = new ImageView();
		icon.setFitHeight(50.0);
		icon.setFitWidth(50.0);

		Label intro1 = new Label("User ");
		Label intro2 = new Label(u.name);
		intro2.setFont(Font.font(intro2.getFont().getName(), FontWeight.BOLD, intro2.getFont().getSize()));
		Label intro3 = new Label(" is...");
		HBox intro = new HBox(intro1, intro2, intro3);
		intro.setAlignment(Pos.CENTER);

		HBox line1 = new HBox(10.0, icon, intro);
		line1.setAlignment(Pos.CENTER);

		Label statusLabel = new Label(u.status.toString());
		statusLabel.setTextFill(getColor(u.status));
		statusLabel.setFont(Font.font(statusLabel.getFont().getName(), FontWeight.BOLD, 24.0));

		ListView<ResultReason> reasonList = new ListView<ResultReason>();
		reasonList.getItems().addAll(u.reasons);
		reasonList.setPrefWidth(400.0);

		Button gotoPage = new Button("Go To FA Page");
		gotoPage.setOnAction((e) -> {
			try {
				Desktop.getDesktop().browse(new URI(u.getUserPageUrl()));
			} catch (IOException | URISyntaxException ex) {
				ex.printStackTrace();
			}
		});
		Button gotoTos = new Button("Go To TOS");
		gotoTos.setDisable(true);
		Button gotoPrices = new Button("Go To Prices");
		gotoPrices.setDisable(true);
		HBox line4 = new HBox(10.0, gotoPage, gotoTos, gotoPrices);
		line4.setAlignment(Pos.CENTER);

		Button close = new Button("Close");

		VBox uservbox = new VBox(10.0, line1, statusLabel, reasonList, line4, close);
		uservbox.setAlignment(Pos.CENTER);
		uservbox.setPadding(new Insets(10.0));

		Stage stage = new Stage();
		Scene scene = new Scene(uservbox);
		stage.setTitle("Who2Comm - " + u.name);
		stage.setScene(scene);
		stage.show();

		close.setOnAction((e) -> {
			stage.close();
		});
	}

	@Override
	public void start(Stage stage) throws Exception {
		Label tab1name = new Label("FA Username:");
		TextField tab1text = new TextField();
		Button tab1go = new Button("Go!");
		HBox tab1hbox = new HBox(10.0, tab1name, tab1text, tab1go);
		tab1hbox.setAlignment(Pos.CENTER);
		BorderPane tab1pane = new BorderPane();
		tab1pane.setPadding(new Insets(10.0));
		tab1pane.setCenter(tab1hbox);
		Tab tab1 = new Tab("Single User Lookup", tab1pane);
		tab1.setClosable(false);

		tab1go.setOnAction((e) -> {
			User u = new User(tab1text.getText());
			u.findIfCommsOpen();
			showDetailWindow(u);
		});
		tab1text.setOnAction((e)->{
			User u = new User(tab1text.getText());
			u.findIfCommsOpen();
			showDetailWindow(u);
			
			tab1text.getParent().requestFocus();
		});

		Label tab2name = new Label("FA Username:");
		TextField tab2text = new TextField();
		Button tab2go = new Button("Go!");
		HBox tab2hbox = new HBox(10.0, tab2name, tab2text, tab2go);
		tab2hbox.setAlignment(Pos.CENTER);
		ListView<User> tab2list = new ListView<User>();
		tab2list.setCellFactory((list) -> {
			return new UserListCell();
		});
		Button tab2clear = new Button("Clear List");
		VBox tab2vbox = new VBox(10.0, tab2hbox, tab2list, tab2clear);
		tab2vbox.setAlignment(Pos.CENTER);
		tab2vbox.setPadding(new Insets(10.0));
		Tab tab2 = new Tab("Lookup From Watchlist", tab2vbox);
		tab2.setClosable(false);

		tab2go.setOnAction((e) -> {
			String[] names = Utils.getWatchlist(tab2text.getText());
			List<User> users = Arrays.asList(names).stream().map((name) -> new User(name)).collect(Collectors.toList());
			tab2list.getItems().clear();
			tab2list.getItems().addAll(users);
		});
		tab2text.setOnAction((e) -> {
			String[] names = Utils.getWatchlist(tab2text.getText());
			List<User> users = Arrays.asList(names).stream().map((name) -> new User(name)).collect(Collectors.toList());
			tab2list.getItems().clear();
			tab2list.getItems().addAll(users);
			
			tab2text.getParent().requestFocus();
		});
		tab2list.setOnMouseClicked((e)->{
			if (e.getClickCount() >= 2) {
				User u = tab2list.getSelectionModel().getSelectedItem();
				u.findIfCommsOpen();
				tab2list.refresh();
				showDetailWindow(u);
			}
		});
		tab2list.setOnKeyPressed((e)->{
			if (e.getCode() == KeyCode.ENTER) {
				User u = tab2list.getSelectionModel().getSelectedItem();
				u.findIfCommsOpen();
				tab2list.refresh();
				showDetailWindow(u);
			}
		});
		tab2clear.setOnAction((e)->{
			tab2list.getItems().clear();
		});

		Label tab3name = new Label("FA Username:");
		TextField tab3text = new TextField();
		Button tab3go = new Button("Go!");
		HBox tab3hbox = new HBox(10.0, tab3name, tab3text, tab3go);
		tab3hbox.setAlignment(Pos.CENTER);
		TableView<User> tab3list = new TableView<User>();
		TableColumn<User, User> col1 = new TableColumn<>("Name");
		col1.setCellFactory((list)->{
			return new UserNameTableCell();
		});
		tab3list.getColumns().add(col1);
		Button tab3clear = new Button("Clear List");
		VBox tab3vbox = new VBox(10.0, tab3hbox, tab3list, tab3clear);
		tab3vbox.setAlignment(Pos.CENTER);
		tab3vbox.setPadding(new Insets(10.0));
		Tab tab3 = new Tab("Watchlist Report", tab3vbox);
		tab3.setClosable(false);
		
		tab3go.setOnAction((e) -> {
			String[] names = Utils.getWatchlist(tab3text.getText());
			List<User> users = Arrays.asList(names).stream().map((name) -> new User(name)).collect(Collectors.toList());
			tab3list.getItems().clear();
			
			for (User user : users) {
				Thread t = new Thread(()->{
					user.findIfCommsOpen();
					tab3list.getItems().add(user);
					tab3list.refresh();
				});
				t.setDaemon(true);
				t.start();
			}
		});
		tab3text.setOnAction((e) -> {
			String[] names = Utils.getWatchlist(tab3text.getText());
			List<User> users = Arrays.asList(names).stream().map((name) -> new User(name)).collect(Collectors.toList());
			tab3list.getItems().clear();
			tab3text.getParent().requestFocus();
			
			for (User user : users) {
				Thread t = new Thread(()->{
					user.findIfCommsOpen();
					tab3list.getItems().add(user);
					tab3list.refresh();
				});
				t.setDaemon(true);
				t.start();
			}
		});
		tab3list.setOnMouseClicked((e)->{
			if (e.getClickCount() >= 2) {
				User u = tab3list.getSelectionModel().getSelectedItem();
				if (u.status == null) return;
				showDetailWindow(u);
			}
		});
		tab3list.setOnKeyPressed((e)->{
			if (e.getCode() == KeyCode.ENTER) {
				User u = tab3list.getSelectionModel().getSelectedItem();
				if (u.status == null) return;
				showDetailWindow(u);
			}
		});
		tab3clear.setOnAction((e)->{
			tab3list.getItems().clear();
		});

		Label tab4name1 = new Label("Delay between page accesses (ms):");
		TextField tab4text1 = new TextField(Long.toString(Settings.MIN_DELAY));
		HBox tab4hbox1 = new HBox(10.0, tab4name1, tab4text1);
		tab4hbox1.setAlignment(Pos.CENTER);
		CheckBox tab4toggle2 = new CheckBox("Use FA authorization?");
		tab4toggle2.setSelected(Settings.USE_AUTH);
		HBox tab4hbox2 = new HBox(10.0, tab4toggle2);
		tab4hbox2.setAlignment(Pos.CENTER);
		Label tab4name3 = new Label("FA authorization cookies:");
		TextField tab4text3 = new TextField(Settings.AUTH_COOKIE);
		HBox tab4hbox3 = new HBox(10.0, tab4name3, tab4text3);
		tab4hbox3.setDisable(!Settings.USE_AUTH);
		tab4hbox3.setAlignment(Pos.CENTER);
		Hyperlink tab4text4 = new Hyperlink("What do I need authorization for?");
		tab4text4.setStyle("-fx-underline: true; -fx-text-fill: blue;");
		HBox tab4hbox4 = new HBox(10.0, tab4text4);
		tab4hbox4.setAlignment(Pos.CENTER);
		VBox tab4vbox = new VBox(10.0, tab4hbox1, tab4hbox2, tab4hbox3, tab4hbox4);
		tab4vbox.setAlignment(Pos.CENTER);
		tab4vbox.setPadding(new Insets(10.0));
		Tab tab4 = new Tab("Settings", tab4vbox);
		tab4.setClosable(false);
		
		tab4text1.setOnAction((e)->{
			tab4text1.getParent().requestFocus();
		});
		tab4text1.focusedProperty().addListener((e, ov, nv)->{
			try {
				Settings.MIN_DELAY = Long.parseLong(tab4text1.getText());
			} catch (NumberFormatException ex) {}
			tab4text1.setText(Long.toString(Settings.MIN_DELAY));
		});
		tab4text3.setOnAction((e)->{
			tab4text3.getParent().requestFocus();
		});
		tab4text3.focusedProperty().addListener((e, ov, nv)->{
			Settings.AUTH_COOKIE = tab4text3.getText();
		});
		tab4toggle2.setOnAction((e)->{
			Settings.USE_AUTH = tab4toggle2.isSelected();
			tab4hbox3.setDisable(!Settings.USE_AUTH);
		});

		TabPane tabs = new TabPane(tab1, tab2, tab3, tab4);
		Scene scene = new Scene(tabs);
		stage.setTitle("Who2Comm");
		stage.setScene(scene);
		stage.show();
	}

	public static void launchGUI() {
		Application.launch(Who2CommGUI.class);
	}
}
