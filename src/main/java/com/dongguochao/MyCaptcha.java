package com.dongguochao;

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

public class MyCaptcha {
    public static final int size = 300;
    public static final int count = 2;
    public static final int blockSize = size/count;

    public static final String workPath = Class.class.getClass().getResource("/").getPath() ;

    // 图片素材
    public static final String sourceImgPath = workPath + "/sourceImg.jpeg";
    // 从图片素材生成的正方形图形
    public static final String squareImgPath = workPath + "/squareImg.png";
    // 被挖坑的图形(问题）
    public static final String questionImagePath = workPath + "/question.jpg";
    // 白色的块（填充到squareImg中，形成questionImagePath)
    public static final String blankBlockImagePath = workPath + "/white.jpg";
    // 生成的多个小块的命名格式
    public static final String blockImagePathPattern = workPath + "/1-{}.png";

    public void cut(int x, int y, int width, int height, String srcpath, String subpath, boolean scale) throws IOException {//裁剪方法
        try (FileInputStream is = new FileInputStream(srcpath); ImageInputStream iis = ImageIO.createImageInputStream(is);){
            Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("jpg"); //ImageReader声称能够解码指定格式
            ImageReader reader = it.next();
            reader.setInput(iis, true); //将iis标记为true（只向前搜索）意味着包含在输入源中的图像将只按顺序读取
            ImageReadParam param = reader.getDefaultReadParam(); //指定如何在输入时从 Java Image I/O框架的上下文中的流转换一幅图像或一组图像
            Rectangle rect = new Rectangle(x, y, width, height); //定义空间中的一个区域
            param.setSourceRegion(rect); //提供一个 BufferedImage，将其用作解码像素数据的目标。
            BufferedImage bi = reader.read(0, param); //读取索引imageIndex指定的对象
            if (scale) {
                // 压缩到120*120
                BufferedImage outputImage = scaleImageWithParams(bi, size, size);
                Graphics g = outputImage.getGraphics();
                g.setColor(new Color(255,255,255));
                g.drawLine(0,150,300,150);// 横线
                g.drawLine(150,0,150,300);// 竖线
                g.dispose();
                ImageIO.write(outputImage, "jpg", new File(subpath)); //保存新图片
            } else {
                ImageIO.write(bi, "jpg", new File(subpath)); //保存新图片
            }
        }
    }

    public static void main(String[] args) throws Exception {

        File picture = new File(sourceImgPath);
        BufferedImage sourceImg = ImageIO.read(new FileInputStream(picture));
        int i = sourceImg.getWidth();
        if (sourceImg.getHeight() < sourceImg.getWidth()) {
            i = sourceImg.getHeight();
        }
        // 随机取60%~100%的区域，保证图片的随机性
        i = i * (60 + new Random().nextInt(40)) / 100;
        // 随机坐标
        int x = new Random().nextInt(sourceImg.getWidth() - i);
        int y = new Random().nextInt(sourceImg.getHeight() - i);

        MyCaptcha pc = new MyCaptcha();
        pc.generateWhite();
        // 从原图中，生成一个正方形的徒刑，并且尺寸设置为size
        pc.cut(x, y, i, i, sourceImgPath, squareImgPath, true);
        // 挖一个白色的坑
        pc.modifyImagetogeter(ImageIO.read(new File(blankBlockImagePath)), ImageIO.read(new File(squareImgPath)));
        // 把一个大的分成4个小的
        for (int q = 0; q < count; q++) {
            for (int p = 0; p < count; p++) {
                pc.cut(blockSize*q, blockSize*p, blockSize, blockSize, squareImgPath, blockImagePathPattern.replace("{}", p+""+q), false);
            }
        }


        System.out.println("ok");
    }

    /***
     * 将图片缩放到指定的高度或者宽度
     * @param width 缩放后的宽度
     * @param height 缩放后的高度
     */
    public static BufferedImage scaleImageWithParams(BufferedImage bufferedImage, int width, int height) {

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

    // 生成替换的块（目前是纯白色的）
    public void generateWhite(){
        int imageWidth = size/count;
        int imageHeight = size/count;
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();
        graphics.setColor(new Color(255, 255, 255));
        graphics.fillRect(0,0,blockSize,blockSize);
        try {
            ImageIO.write(image, "jpg", new File(blankBlockImagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        graphics.dispose();
    }
}