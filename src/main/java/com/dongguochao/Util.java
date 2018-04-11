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

public class Util {
    private Util() {
    }

    // 生成替换的块
    public static void generateWhite(int size, String outputPath, Color color) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();
        graphics.setColor(color);
        graphics.fillRect(0, 0, size, size);
        try {
            ImageIO.write(image, "jpg", new File(outputPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        graphics.dispose();
    }

    // 生成替换的块（目前是纯白色的）
    public static void generateWhite(int size, String outputPath) {
        generateWhite(size, outputPath, new Color(255, 255, 255));
    }

    /**
     * 挖坑
     *
     * @param x
     * @param y
     * @param width
     * @param height
     * @param srcpath
     * @return
     * @throws IOException
     */
    public static BufferedImage cutImage(int x, int y, int width, int height, String srcpath) throws IOException {//裁剪方法
        try (FileInputStream is = new FileInputStream(srcpath); ImageInputStream iis = ImageIO.createImageInputStream(is)) {
            Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("jpg"); //ImageReader声称能够解码指定格式
            ImageReader reader = it.next();
            reader.setInput(iis, true); //将iis标记为true（只向前搜索）意味着包含在输入源中的图像将只按顺序读取
            ImageReadParam param = reader.getDefaultReadParam(); //指定如何在输入时从 Java Image I/O框架的上下文中的流转换一幅图像或一组图像
            Rectangle rect = new Rectangle(x, y, width, height); //定义空间中的一个区域
            param.setSourceRegion(rect); //提供一个 BufferedImage，将其用作解码像素数据的目标。
            return reader.read(0, param); //读取索引imageIndex指定的对象
        }
    }

    /***
     * 将图片缩放到指定的高度或者宽度
     * @param width 缩放后的宽度
     * @param height 缩放后的高度
     */
    public static BufferedImage scaleImage(BufferedImage bufferedImage, int width, int height) {
        try {
            Image image = bufferedImage.getScaledInstance(width, height,
                    Image.SCALE_DEFAULT);
            BufferedImage outputImage = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics graphics = outputImage.getGraphics();
            graphics.drawImage(image, 0, 0, null);
            graphics.dispose();
            return outputImage;
        } catch (Exception e) {
            System.out.println("scaleImageWithParams方法压缩图片时出错了");
            e.printStackTrace();
            return null;
        }
    }

    public static char getRandomHanzi() {
        return (char) (0x4e00 + (int) (Math.random() * (0x9fa5 - 0x4e00 + 1)));
    }

    /**
     * 将彩色图片变为灰色的图片
     *
     */
    public static BufferedImage grayImage(BufferedImage bufImg) {
        int[][] result = getImageGRB(bufImg);
        int[] rgb = new int[3];
        BufferedImage bi = new BufferedImage(result.length, result[0].length, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                rgb[0] = (result[i][j] & 0xff0000) >> 16;
                rgb[1] = (result[i][j] & 0xff00) >> 8;
                rgb[2] = (result[i][j] & 0xff);
                int color = (int) (rgb[0] * 0.3 + rgb[1] * 0.59 + rgb[2] * 0.11);
                bi.setRGB(i, j, (color << 16) | (color << 8) | color);
            }
        }
        return bi;
    }

    /**
     * 获取图片的像素点
     *
     * @return
     */
    private static int[][] getImageGRB(BufferedImage bufImg) {
        int[][] result;
        int height = bufImg.getHeight();
        int width = bufImg.getWidth();
        result = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                result[i][j] = bufImg.getRGB(i, j) & 0xFFFFFF;
            }
        }
        return result;
    }

    public static int[] getImageSize(String sourceImgPath){
        final ImageInfo imageInfo;
        try {
            imageInfo = Sanselan.getImageInfo(new File(sourceImgPath));
        } catch (ImageReadException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return new int[]{imageInfo.getWidth(), imageInfo.getHeight()};
    }
}
