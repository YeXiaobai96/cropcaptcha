package com.dongguochao;

import org.apache.sanselan.ImageInfo;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;

/**
 * 拼图验证码
 */
public class CropCaptcha {
    public static final int size = 300;
    public static final int count = 2;
    public static final int blockSize = size/count;

    public static final String workPath = Class.class.getClass().getResource("/").getPath() ;

    // 图片素材
    public static final String sourceImgPath = workPath + "/sourceImg.jpeg";
    // 从图片素材生成的正方形图形
    public static final String squareImgPath = workPath + "/question-raw.png";
    // 被挖坑的图形(问题）
    public static final String questionImagePath = workPath + "/question-crop.jpg";
    // 白色的块（填充到squareImg中，形成questionImagePath)
    public static final String blankBlockImagePath = workPath + "/white.jpg";
    // 生成的多个小块的命名格式
    public static final String blockImagePathPattern = workPath + "/1-{}.png";


    public static void main(String[] args) throws Exception {

        int[] imageSize = Util.getImageSize(sourceImgPath);

        int sourceWidth = imageSize[0];
        int sourceHeight = imageSize[1];

        int targetSize = sourceWidth;
        if (sourceHeight < sourceWidth) {
            targetSize = sourceHeight;
        }
        // 随机取60%~100%的区域，保证图片的随机性
        targetSize = targetSize * (60 + new Random().nextInt(40)) / 100;
        // 随机坐标
        int x = new Random().nextInt(sourceWidth - targetSize);
        int y = new Random().nextInt(sourceHeight - targetSize);

        CropCaptcha pc = new CropCaptcha();
        Util.generateWhite(blockSize, blankBlockImagePath);
        // 从原图中，生成一个正方形的徒刑，并且尺寸设置为size
        BufferedImage bi = Util.cutImage(x, y, targetSize, targetSize, sourceImgPath);
        // 压缩到120*120
        BufferedImage outputImage = Util.scaleImage(bi, size, size);
        Graphics g = outputImage.getGraphics();
        // 画出分割线，方便肉眼识别
        g.setColor(new Color(255,255,255));
        g.drawLine(0,150,300,150);// 横线
        g.drawLine(150,0,150,300);// 竖线
        g.dispose();
        ImageIO.write(outputImage, "jpg", new File(squareImgPath)); //保存新图片

        // 挖一个白色的坑
        pc.modifyImagetogeter(ImageIO.read(new File(blankBlockImagePath)), ImageIO.read(new File(squareImgPath)));
        // 把一个大的分成count*count个小的
        for (int q = 0; q < count; q++) {
            for (int p = 0; p < count; p++) {
                BufferedImage pieceBi = Util.cutImage(blockSize*q, blockSize*p, blockSize, blockSize, squareImgPath);
                ImageIO.write(pieceBi, "jpg", new File(blockImagePathPattern.replace("{}", p+""+q)));
            }
        }

        System.out.println("ok");
    }

    public void modifyImagetogeter(BufferedImage b, BufferedImage d) {

        try {
            Graphics g = d.createGraphics();
            int randomX = new Random().nextInt(count)*blockSize;
            int randomY = new Random().nextInt(count)*blockSize;
            g.drawImage(b, randomX, randomY, blockSize, blockSize, null);
            g.dispose();
            ImageIO.write(d, "jpg", new File(questionImagePath));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}