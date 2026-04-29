package com.github.jpeg2000.imageio;

import java.io.IOException;
import java.util.Locale;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

/**
 * JPEG2000 ImageIO Writer SPI. 
 */
public class ImageWriterImpl extends ImageWriterSpi
{
    @Override
    public Class[] getOutputTypes()
    {
        return new Class[] {ImageOutputStream.class};
    }
    
    @Override
    public ImageWriter createWriterInstance(Object extension) throws IOException
    {
        return new JPEG2000Writer(this);
    }

    @Override
    public String[] getFormatNames()
    {
        return new String[] {"jpeg2000", "JPEG2000"};
    }
    
    @Override
    public String[] getFileSuffixes()
    {
        return new String[] {"jp2", "j2k"};
    }
    
    @Override
    public String getDescription(Locale locale)
    {
        return "JPEG2000 ImageIO Writer Support";
    }

    @Override
    public boolean canEncodeImage(ImageTypeSpecifier type)
    {
        return true;
    }

}
