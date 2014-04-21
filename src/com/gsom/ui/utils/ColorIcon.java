/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gsom.ui.utils;

//
//
//   ColorIcon
//
//   Copyright (C) by Andrea Carboni.
//   This file may be distributed under the terms of the LGPL license.
//
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.Icon;

//
public class ColorIcon implements Icon {

    private int iWidth;
    private int iHeight;
    private Color color;
    //private Color border;
    private Insets insets;

    //---------------------------------------------------------------------------
    public ColorIcon() {
        this(32, 16);
    }

    //---------------------------------------------------------------------------
    public ColorIcon(int width, int height) {
        this(width, height, Color.black);
    }

    //---------------------------------------------------------------------------
    public ColorIcon(int width, int height, Color c) {
        iWidth = width;
        iHeight = height;

        color = c;
        insets = new Insets(1, 1, 1, 1);
    }

    //---------------------------------------------------------------------------
    public void setColor(Color c) {
        color = c;
    }

    //---------------------------------------------------------------------------
    public Color getColor() {
        return color;
    }


    //---------------------------------------------------------------------------
    //---
    //--- Icon interface methods
    //---
    //---------------------------------------------------------------------------
    public int getIconWidth() {
        return iWidth;
    }

    //---------------------------------------------------------------------------
    public int getIconHeight() {
        return iHeight;
    }

    //---------------------------------------------------------------------------
    public void paintIcon(Component c, Graphics g, int x, int y) {

        x += insets.left;
        y += insets.top;

        int w = iWidth - insets.left - insets.right;
        int h = iHeight - insets.top - insets.bottom - 1;

        g.setColor(color);
        g.fillRect(x, y, w, h);
    }
}
