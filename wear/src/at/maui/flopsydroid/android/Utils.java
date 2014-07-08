package at.maui.flopsydroid.android;

import android.content.Context;
import android.view.Menu;

import java.lang.reflect.Constructor;

/**
 * Created by maui on 07.07.14.
 */
public class Utils {
    private static Class<?> menuBuilderClass;
    private static Constructor<?> menuConstructor;

    public static Menu newMenuInstance(Context context) {
        try {

            if(menuConstructor == null) {
                menuBuilderClass = Class.forName("com.android.internal.view.menu.MenuBuilder");
                menuConstructor = menuBuilderClass.getDeclaredConstructor(Context.class);
            }

            Menu m = (Menu) menuConstructor.newInstance(context);

            return m;

        } catch (Exception e) {

        }

        return null;
    }
}
