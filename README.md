# who2comm

Who's open for commissions on FurAffinity? Find out now!

Who2comm is a tool to look up users on FA and discover if they are currently open for commissions or not.

## Download

Get the latest, bleeding-edge build at [my drone.io page](https://drone.io/github.com/iconmaster5326/who2comm/files)!

`who2comm.exe` is a Windows standalone executable. You'll still need Java to run it.

`who2comm.jar` is the Java JAR file. This is platform-independant!

## Running

This program requires Java 8 to run. If you don't have Java, download it at [https://java.com/](https://java.com/).

You have to run this program on the command line (To do this easily on Windows, shift-right-click on the directory who2comm is in, and click "Open Command Window Here").

If you use `who2comm.exe`, run:

    ./who2comm.exe

If you use `who2comm.jar`, run:

    java -jar who2comm.jar

## Options

If supplied without any arguments, who2comm will prompt you for a FA username to look up. With one argument supplied, who2comm will look up that user instead.

The flags currently supported are:
 * __-auth__: Provide a cookie set for FA authorization (see below)
 
## Usage

Once it has the name of a FA user, it will scrape their FA userpage and try to see if they are open for commissions or not.

It will produce a list of reasons why it thinks this user is open or not. It checks places such as journals and profile blurbs.

Who2comm is not perfect! Always inspect the results returned to see if the user is actually open or not before sending that note.

## Authorization

Some FA users have set up their accounts so people not logged in cannot see their profile. To let who2comm inspect these users, you will need to provide who2comm with your FA login session.

To authourize who2comm, you first need to grab your cookie information. To do this, open your browser, open the developer console (press F12 on Chrome), and execute the following JavaScript snippet:

    document.cookie.split(';').filter(function(x){return/^[ab]=/.test(x)}).sort().reverse().join(';')

You should get a string as output. Copy this string, and provide it to the `-auth` flag of who2comm (see above).

## Compiling

If you want to manually produce who2comm from source, just check out the repository and run

    ./gradlew jar launch4j

If you're running Windows and don't have Cygwin, etc., run `gradlew.bat` instead of `./gradlew`.

This will produce both files, in `build/libs` and `build/launch4j`, respectively.

## Bug Reports

I'd really like to know when an execution results in a false positive or a false negative. Please use the [GitHub issue tracker](https://github.com/iconmaster5326/who2comm/issues) to submit and bug reports and false results.

## Coming Soon

 * A GUI interface!
 * More sophisticated determination algorithms.
 * Ability to load a watchlist and check all artists at once.
 * Automatically finding links to TOSes and price guides.
 
# DISCLAIMER

THIS APPLICATION DOES NOT PROVIDE A WARRANTY FOR ANY FUNCTIONALITY. THE USER OF THIS APPLICATION IS LIABLE FOR ANY VIOLATIONS OF THE FURAFFINITY [TERMS OF SERVICE](http://www.furaffinity.net/tos) OR [CODE OF CONDUCT](http://www.furaffinity.net/coc) DONE WHILE USING THIS APPLICATION.