
package com.clinicas.util;
import javax.swing.*; import java.awt.Font; import java.util.Enumeration;
public class UITheme {
  public static void boostFonts(int delta){
    UIDefaults d=UIManager.getLookAndFeelDefaults();
    for(Enumeration<Object> e=d.keys(); e.hasMoreElements();){
      Object k=e.nextElement(); Object v=d.get(k);
      if(v instanceof Font f){ d.put(k, f.deriveFont((float)(f.getSize()+delta))); }
    }
  }
}
