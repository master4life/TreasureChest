package de.kiyan.TreasureChest.Utils;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;


public class Utils {
    public static int RandInt( int paramInt1, int paramInt2 ) {
        return paramInt1 + ( int ) ( Math.random() * ( paramInt2 - paramInt1 + 1 ) );
    }

    public static Location getBlockCenterUP( Location paramLocation ) {
        if( paramLocation != null )
            return new Location( paramLocation.getWorld(), paramLocation.getX() + 0.5D, paramLocation.getY() + 1.0D, paramLocation.getZ() + 0.5D );
        return paramLocation;
    }

    public static Location getBlockCenter( Location paramLocation ) {
        if( paramLocation != null )
            return new Location( paramLocation.getWorld(), paramLocation.getX() + 0.5D, paramLocation.getY(), paramLocation.getZ() + 0.5D );
        return paramLocation;
    }

    public static float getLookAtYaw( Vector vector ) {
        double d1 = vector.getX();
        double d2 = vector.getZ();
        double d3 = 0.0D;
        if( d1 != 0.0D ) {
            if( d1 < 0.0D ) {
                d3 = 4.71238898038469D;
            } else {
                d3 = 1.5707963267948966D;
            }
            d3 -= Math.atan( d2 / d1 );
        } else if( d2 < 0.0D ) {
            d3 = Math.PI;
        }
        return ( float ) ( -d3 * 180.0D / Math.PI - 90.0D );
    }

    public static float normalAngle( float fl ) {
        for( ; fl <= -180.0F; fl += 360.0F ) ;
        for( ; fl > 180.0F; fl -= 360.0F ) ;
        return fl;
    }

    public static BlockFace yawToFace( float fl, boolean paramBoolean ) {
        fl -= 90.0F;
        fl = normalAngle( fl );
        if( paramBoolean ) {
            switch( ( int ) fl ) {
                case 0:
                    return BlockFace.NORTH;
                case 45:
                    return BlockFace.NORTH_EAST;
                case 90:
                    return BlockFace.EAST;
                case 135:
                    return BlockFace.SOUTH_EAST;
                case 180:
                    return BlockFace.SOUTH;
                case 225:
                    return BlockFace.SOUTH_WEST;
                case 270:
                    return BlockFace.WEST;
                case 315:
                    return BlockFace.NORTH_WEST;
            }
            if( fl >= -22.5D && fl < 22.5D )
                return BlockFace.NORTH;
            if( fl >= 22.5D && fl < 67.5D )
                return BlockFace.NORTH_EAST;
            if( fl >= 67.5D && fl < 112.5D )
                return BlockFace.EAST;
            if( fl >= 112.5D && fl < 157.5D )
                return BlockFace.SOUTH_EAST;
            if( fl >= -67.5D && fl < -22.5D )
                return BlockFace.NORTH_WEST;
            if( fl >= -112.5D && fl < -67.5D )
                return BlockFace.WEST;
            if( fl >= -157.5D && fl < -112.5D )
                return BlockFace.SOUTH_WEST;
            return BlockFace.SOUTH;
        }
        switch( ( int ) fl ) {
            case 0:
                return BlockFace.NORTH;
            case 90:
                return BlockFace.EAST;
            case 180:
                return BlockFace.SOUTH;
            case 270:
                return BlockFace.WEST;
        }
        if( fl >= -45.0F && fl < 45.0F )
            return BlockFace.NORTH;
        if( fl >= 45.0F && fl < 135.0F )
            return BlockFace.EAST;
        if( fl >= -135.0F && fl < -45.0F )
            return BlockFace.WEST;
        return BlockFace.SOUTH;
    }
}
