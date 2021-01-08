package de.kiyan.ChangeSkin.Util;

import com.google.common.collect.Lists;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Digits {
    HashMap< String, ArrayList< Pair<Integer,Integer>>> numbers = null;

    ArrayList<Pair< Integer, Integer>> ZERO = Lists.newArrayList(
            new Pair( 2, 0 ),
            new Pair( 2, 1),
            new Pair( 2, 2),
            new Pair( 2,3),
            new Pair( 2,4),
            new Pair( 1,0),
            new Pair( 1,4),
            new Pair( 0,0),
            new Pair( 0,1),
            new Pair( 0,2),
            new Pair( 0,3),
            new Pair( 0,4)
    );
    ArrayList<Pair< Integer, Integer>> ONE = Lists.newArrayList(
            new Pair( 1,0),
            new Pair( 1,1),
            new Pair( 1,2),
            new Pair( 1,3),
            new Pair( 1,4)
    );
    ArrayList<Pair< Integer, Integer>> TWO = Lists.newArrayList(
            new Pair( 2,0),
            new Pair( 2,1),
            new Pair( 2,2),
            new Pair( 2, 4),
            new Pair( 1,0),
            new Pair( 1,2),
            new Pair( 1,4),
            new Pair( 0,0),
            new Pair( 0,2),
            new Pair( 0,3),
            new Pair( 0,4)
    );
    ArrayList<Pair< Integer, Integer>> THREE = Lists.newArrayList(
            new Pair( 2,0),
            new Pair( 2,2),
            new Pair( 2,4),
            new Pair( 1,0),
            new Pair( 1,2),
            new Pair( 1,4),
            new Pair( 0,0),
            new Pair( 0,1),
            new Pair( 0,2),
            new Pair( 0,3),
            new Pair( 0,4)
    );
    ArrayList<Pair< Integer, Integer>> FOUR = Lists.newArrayList(
            new Pair( 2,2),
            new Pair( 2,3),
            new Pair( 2,4),
            new Pair( 1,2),
            new Pair( 0,0),
            new Pair( 0,1),
            new Pair( 0,2),
            new Pair( 0,3),
            new Pair( 0,4)
    );
    ArrayList<Pair< Integer, Integer>> FIVE = Lists.newArrayList(
            new Pair( 2,0),
            new Pair( 2,2),
            new Pair( 2,3),
            new Pair( 2,4),
            new Pair( 1,0),
            new Pair( 1,2),
            new Pair( 1,4),
            new Pair( 0,0),
            new Pair( 0,1),
            new Pair( 0,2),
            new Pair( 0,4)
    );
    ArrayList<Pair< Integer, Integer>> SIX = Lists.newArrayList(
            new Pair( 2,0),
            new Pair( 2,1),
            new Pair( 2,2),
            new Pair( 2,3),
            new Pair( 2,4),
            new Pair( 1,0),
            new Pair( 1,2),
            new Pair( 1,4),
            new Pair( 0,0),
            new Pair( 0,1),
            new Pair( 0,2),
            new Pair( 0,4)
    );

    ArrayList<Pair< Integer, Integer>> SEVEN = Lists.newArrayList(
            new Pair( 2,3),
            new Pair( 2,4),
            new Pair( 1,4),
            new Pair( 0,0),
            new Pair( 0,1),
            new Pair( 0,2),
            new Pair( 0,3),
            new Pair( 0,4)
    );

    ArrayList<Pair< Integer, Integer>> EIGHT = Lists.newArrayList(
            new Pair( 2,0),
            new Pair( 2,1),
            new Pair( 2,2),
            new Pair( 2,3),
            new Pair( 2,4),
            new Pair( 1,0),
            new Pair( 1,2),
            new Pair( 1,4),
            new Pair( 0,0),
            new Pair( 0,1),
            new Pair( 0,2),
            new Pair( 0,3),
            new Pair( 0,4)
    );
    ArrayList<Pair< Integer, Integer>> NINE = Lists.newArrayList(
            new Pair( 2,2),
            new Pair( 2,3),
            new Pair( 2,4),
            new Pair( 1,2),
            new Pair( 1,4),
            new Pair( 0,0),
            new Pair( 0,1),
            new Pair( 0,2),
            new Pair( 0,3),
            new Pair( 0,4)
    );

    public Digits()
    {
        if( numbers == null )
        {
            HashMap< String, ArrayList< Pair<Integer,Integer>> > map = new HashMap<>();
            map.put( "0", ZERO );
            map.put( "1", ONE );
            map.put( "2", TWO );
            map.put( "3", THREE );
            map.put( "4", FOUR );
            map.put( "5", FIVE );
            map.put( "6", SIX );
            map.put( "7", SEVEN );
            map.put( "8", EIGHT );
            map.put( "9", NINE );

            this.numbers = map;
        }
    }

    public ArrayList<ArrayList<Pair<Integer, Integer>>> getNumbers (int number) {
        ArrayList numCoords = Lists.newArrayList();
        char[] digits = String.valueOf( number ).toCharArray();
        if(digits.length < 2)
            numCoords.add( numbers.get( "0") );

        for( int i = 0; i < digits.length; i++ )
        {
            numCoords.add( numbers.get(String.valueOf(digits[ i ] )));
        }

        return numCoords;
    }

    public BufferedImage drawNumber(BufferedImage image, ArrayList<ArrayList<Pair<Integer, Integer>>> numberCoords) {
        AtomicInteger xCoordPos = new AtomicInteger(35);
        numberCoords.forEach( numb -> {
                    if( numb != null )
                    {
                        int xCoord = xCoordPos.getAndAdd(4);
                        numb.forEach(pair-> image.setRGB( xCoord - pair.getLeft(), 42 - pair.getRight(), Color.BLACK.getRGB()));
                    }
        });
        return image;
    }
}
