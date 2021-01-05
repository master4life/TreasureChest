package de.kiyan.TreasureChest.Utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.*;

public class ItemStackUtils {
    private static String getEnchants( ItemStack itemStack ) {
        ArrayList< String > arrayList = new ArrayList();
        Map< Enchantment, Integer > map = itemStack.getEnchantments();
        for( Enchantment enchantment : map.keySet() )
            arrayList.add( String.valueOf( enchantment.getName() ) + "#" + map.get( enchantment ) );

        String string = "";
        for( String str1 : arrayList ) {
            if( string.equals( "" ) ) {
                string = str1;
                continue;
            }
            string = String.valueOf( string ) + "," + str1;
        }
        return string;
    }

    private static String getLores( ItemStack paramItemStack ) {
        List< String > list = paramItemStack.getItemMeta().getLore();

        if( list == null )
            list = new ArrayList<>();

        String str = "";

        for( String str1 : list ) {
            if( str.equals( "" ) ) {
                str = str1;
                continue;
            }
            str = String.valueOf( str ) + "," + str1;
        }
        return str;
    }

    public static String serialize( ItemStack item ) { return serialize( item, "§f§lcommon" ); }
    public static String serialize( ItemStack item, String tier ) {
        String[] arrayOfString1 = new String[ 8 ];
        arrayOfString1[ 0 ] = item.getType().name();
        arrayOfString1[ 1 ] = Integer.toString( item.getAmount() );
        arrayOfString1[ 2 ] = String.valueOf( item.getDurability() );

        if( item.getItemMeta().hasDisplayName() ) {
            arrayOfString1[ 3 ] = item.getItemMeta().getDisplayName();
        } else {
            arrayOfString1[ 3 ] = "";
        }
        arrayOfString1[ 4 ] = String.valueOf( item.getData().getData() );
        arrayOfString1[ 5 ] = getEnchants( item );
        arrayOfString1[ 6 ] = getLores( item );
        arrayOfString1[ 7 ] = tier;
        String str = "";
        byte b;
        int i;
        String[] arrayOfString2;
        for( i = ( arrayOfString2 = arrayOfString1 ).length, b = 0; b < i; ) {
            String str1 = arrayOfString2[ b ];
            if( str.equals( "" ) ) {
                str = str1;
            } else {
                str = String.valueOf( str ) + ";" + str1;
            }
            b++;
        }
        return str;
    }

    public static ItemStack deserialize( String string ) {  return deserialize( string, false ); }
    public static ItemStack deserialize( String string, Boolean withTier ) {
        String[] arrayOfString = string.split( ";" );

        ItemStack itemStack = new ItemStack( Material.getMaterial( arrayOfString[ 0 ] ), Integer.parseInt( arrayOfString[ 1 ] ) );
        itemStack.setDurability( ( short ) Integer.parseInt( arrayOfString[ 2 ] ) );

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName( arrayOfString[ 3 ] );

        MaterialData materialData = itemStack.getData();
        materialData.setData( ( byte ) Integer.parseInt( arrayOfString[ 4 ] ) );
        itemStack.setData( materialData );
        List< String > lores = new LinkedList<>();;
        if( !arrayOfString[ 5 ].equalsIgnoreCase( "" ) ) {
            String[] enchantmenFULL = arrayOfString[ 5 ].split( "," );
            byte b;
            int i;
            String[] arrayOfString2;
            for( i = ( arrayOfString2 = enchantmenFULL ).length, b = 0; b < i; ) {
                String lore = arrayOfString2[ b ];
                String enchant = lore.split( "#" )[ 0 ];
                String enchantLevel = null;

                if( ( lore.split( "#" ) ).length == 2 )
                    enchantLevel = lore.split( "#" )[ 1 ];

                Enchantment enchantment = Enchantment.getByName( enchant );
                if( enchantment != null ) {
                    int j = 0;
                    try
                    {
                        j = Integer.parseInt( enchantLevel );
                    }
                    catch( Exception exception ) { }

                    itemMeta.addEnchant( enchantment, j, true );
                }
                b++;

            }

        }
        if( !arrayOfString[ 6 ].equalsIgnoreCase( "" ) )
        {
            for( String singleLore : arrayOfString[ 6 ].split( "," ) )
                lores.add(  singleLore );
            itemMeta.setLore( lores );
        }

        if( withTier && !arrayOfString[ 7 ].equalsIgnoreCase( "" ) )
        {
            lores.add( arrayOfString[ 7 ] );
            itemMeta.setLore( lores );
        }

        itemStack.setItemMeta( itemMeta );
        return itemStack;
    }
}
