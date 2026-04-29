package com.github.jpeg2000.imageio;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

/**
 * Checks for JPEG2000 and creates the nessesary Reader. 
 */
public class ImageReaderImpl extends ImageReaderSpi
{
    private static final byte[] magicNumber = new byte[] 
    {
        0 , 0 , 0, 12, 106, 80
    };
    
    @Override
    public boolean canDecodeInput(Object source) throws IOException
    {
        if(source instanceof ImageInputStream)
        {
            ImageInputStream in = (ImageInputStream) source;
            in.mark();
            byte[] magicNumber = new byte[6];
            in.readFully(magicNumber);
            in.reset();
            return Arrays.equals(this.magicNumber, magicNumber);
        }
        return false;
    }

    @Override
    public Class[] getInputTypes()
    {
        return new Class[] {ImageInputStream.class};
    }
    
    @Override
    public ImageReader createReaderInstance(Object extension) throws IOException
    {
        return new JPEG2000Reader(this);
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
        return "JPEG2000 ImageIO Reader Support";
    }

}
