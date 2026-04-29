jpeg2000
====================

This is another version of the "JJ2000" package, which provides an API for reading and writing JPEG-2000 encoded image data in Java. It is based on the "JAI" branch of the code, but with the JAI dependencies removed so it can be compiled as a standalone package again. The box model has been drastically simplified replacing the very heavy one dating from the JAI code, and simpler classes for reading/writing JPX images have been introduced, that focus on reading/writing to InputStream and OutputStream wherever possible rather than to a File or IIOImage.

How to build
------------
Download and run "ant". The jar "target/jj2000.jar" contains the API code, the "target/test.jar" is a standalone Jar for testing (run "java -jar target/test.jar" for help). There are no external dependencies

ImageIO support
---------------
The Jar supplies an ImageIO support, so reading is as simple as 

`javax.imageio.ImageIO.read(new File("input.jp2"))`

and for writing 

`ImageIO.write(image, "JPEG2000", new File("image.jp2"));`

(thanks to @keinhaar for the PR). Both, reader and writer, do not support metadata.

How to read a JP2 or JPX image
------------------------------
For non-ImageIO use, this code will create a PNM from a grayscale or RGB image.
```java
import java.io.*;
import com.github.jpeg2000.*;
import jj2000.j2k.io.*;

File infile = new File("in.jp2");
File outfile = new File("out.pnm");
J2KFile file = new J2KFile();
file.read(new BEBufferedRandomAccessFile(infile, "r", 8192));
J2KReader iin = new J2KReader(file);
OutputStream out = new BufferedOutputStream(new FileOutputStream(outfile));
if (iin.getNumComponents() == 1) {
    out.write(("P5 "+iin.getWidth()+" "+iin.getHeight()+" 255\n").getBytes("ISO-8859-1"));
} else if (iin.getNumComponents() == 3) {
    out.write(("P6 "+iin.getWidth()+" "+iin.getHeight()+" 255\n").getBytes("ISO-8859-1"));
}
int c;
while ((c=iin.read()) >= 0) {
    out.write(c);
}
out.close();
```
and this will save the file as a PNG
```java
import java.io.*;
import com.github.jpeg2000.*;
import jj2000.j2k.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

File infile = new File("in.jp2");
File outfile = new File("out.png");
J2KFile file = new J2KFile();
file.read(new BEBufferedRandomAccessFile(infile, "r", 8192));
J2KReader iin = new J2KReader(file);
BufferedImage image = iin.getBufferedImage();
ImageIO.write(image, "png", outfile);
```
This second example presume the JP2 file is grayscale, RGB, indexed-RGB or has an embedded ICC profile. CMYK, Lab and other spaces need a `java.awt.ColorSpace` implementation - you can override the `J2KReader.createColorSpace` to supply one of these if you have an implementation.

How to write a JP2 file
------------------------------
This will create a JP2 from a grayscale or RGB image.
```java
import java.io.*;
import com.github.jpeg2000.*;
import jj2000.j2k.io.*;


BufferedImage image = ...
J2KWriter writer = new J2KWriter();
writer.setCompressionRatio(8, false);
writer.setSource(image, 256);
writer.write(new FileOutputStream("out.jp2"));
```

License
--------------------
The JJ2000 portion of the code is covered under the  [JJ2000](LICENSE-JJ2000.txt) license. The JAI portions of the code have mostly been removed, although some contributions to the main body of the API may remain: they are covered under a modified [BSD](LICENSE-Sun.txt) license. The BFO contributions (mainly in `com.github.jpeg2000`, but again with some contributions to the main body of the API) are licensed under the same modified BSD license.

History (which may be wrong)
----------------------------
The JJ2000 package was originally written by a team from Swiss Federal Institute of Technology-EPFL, Ericsson Radio Systems AB and Canon Research Centre France S.A during 1999-2000 as part of the development of the original JPEG2000 specification. The source code was made available at http://jpeg2000.epfl.ch/ and the final release there was version 5.1 (this site disappeared around 2010; an archive version is available at http://web.archive.org/web/20100818165144/http://jpeg2000.epfl.ch/)

The code was then adopted by Sun as part of their JAI project (Java Advanced Imaging). It was hosted at https://jai-imageio.dev.java.net/, with  changes made by Sun to fit into their JAI architecture. This eventually shut down too (disappearing completely in 2016 after a long period of bitrot) and the code was migrated to Github and https://github.com/jai-imageio/jai-imageio-jpeg2000 in April 2010, where this fork came from. The [JAI project](https://github.com/jai-imageio/) is still active.

The original pre-JAI code from JJ2000 also moved, to https://code.google.com/archive/p/jj2000/. From there it was copied to Github at https://github.com/Unidata/jj2000, and probably elsewhere too.

The two codebases diverged slightly; the "ucar" build (derived from the original JJ2000 codebase) had issues with failing to read the last tile from JP2 streams where the number of tiles was listed as one less than required; common in many of our test files. This has been [Patched](https://github.com/Unidata/jj2000/pull/8). The "jai" build had issues with an integer overflow, usually causing black blobs on the image. This has also been [Patched](https://github.com/jai-imageio/jai-imageio-jpeg2000/pull/24), although at the time of writing the pull request has not been merged, because maven.

Although the two packages are largely identical in terms of API and functionality, we have found the "JAI" branch to use less memory overall for the kind of files where this is an issue (large, high resolution, single tile images - the differences seems to stem from the changes in jj2000.j2k.codestream.reader.FileBitstreamReaderAgent). So we've chosen to base this branch on that version.
