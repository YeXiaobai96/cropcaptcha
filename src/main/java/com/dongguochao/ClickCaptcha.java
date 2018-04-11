package com.dongguochao;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * 点击文字验证码
 */
public class ClickCaptcha {

    public static final String workPath = Class.class.getClass().getResource("/").getPath();

    public static final int fontCount = 3;
    public static final int fontSize = 30;

    public static final String questionImagePath = workPath + "/question-click.jpg";
    // 图片素材
    public static final String sourceImgPath = workPath + "/sourceImg.jpeg";

    public static void main(String[] a) {

        int[] imageSize = Util.getImageSize(sourceImgPath);

        // 汉字画到图上
        BufferedImage bi;
        try {
            bi = ImageIO.read(new File(sourceImgPath));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        char[] hanzi = new char[fontCount];
        int[][] location = new int[fontCount][2];
        BufferedImage bi2;
        if(imageSize[0]>imageSize[1]){
            bi2 = Util.scaleImage(bi, 600, imageSize[1]*600/imageSize[0]);
        }else{
            bi2 = Util.scaleImage(bi, imageSize[0]*600/imageSize[1], 600);
        }

        Graphics2D g = bi2.createGraphics();
        g.setFont(new Font("TimesRoman", Font.BOLD, 40));
        g.setColor(Color.BLACK);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

        for (int i = 0; i < hanzi.length; i++) {
            hanzi[i] = Util.getRandomHanzi();
            location[i][0] = new Random().nextInt(imageSize[0]-fontSize);
            location[i][1] = new Random().nextInt(imageSize[1]-fontSize);
            g.drawString(Character.toString(hanzi[i]), location[i][0], location[i][1]);
            System.out.println(hanzi[i]);
        }
        g.dispose();
        try {
            ImageIO.write(bi2, "jpg", new File(questionImagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
