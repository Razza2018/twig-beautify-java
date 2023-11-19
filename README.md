# twig-beautify

This is a formatter for twig code.

It can:
- Beautify one line code with proper newlines and indents.
- Remove all indents and newlines to convert to one line.
- Add whitespace control characters (-) to all tags so spaces don't print in html forms.

When you beautify, it also removes any leading or trailing concatenation characters (~) where they're not meant to be.

## To Build
**Tools Needed**
- jre - `java` command in terminal. To check type `java --version`.
- jdk - `javac` command in terminal. To check type `javac --version`.
- jar - `jar` command in terminal. To check type `jar --version`.

**Build .jar**
- Create a 'build' directory where your TwigBeautify.java file is located. `md build`.
- Compile the .java file into .class files. `javac -d build TwigBeautify.java`.
- Navigate into the build directory. (Important otherwise the build directory will be included in the .jar and the entry-point won't work correctly.)
- Package your classes into a .jar file. `jar cfe TwigBeautify.jar TwigBeautify *.class`.

**Run .jar**
- Run the compiled .class files with `java TwigBeautify` in the build directory. This can be used to test the programme.
- Run the packaged .jar file with `java -jar TwigBeautify.jar` in the build directory. (Can be moved to anywhere else now.)
- Run from your file exploerer by double clicking on the .jar file. This should run the file using the jre. 
