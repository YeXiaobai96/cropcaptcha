package com.dongguochao;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * 滑块验证码
 * // todo 现在是正方形，需要做成异形的块
 */
public class SlideCaptcha {

    public static final String workPath = Class.class.getClass().getResource("/").getPath() ;

    public static final int blockSize = 80;

    public static final String questionImagePath = workPath + "/question-slide.jpg";
    public static final String questionRawImagePath = workPath + "/question-slide-raw.jpg";
    // 图片素材
    public static final String sourceImgPath = workPath + "/sourceImg.jpeg";
    // 白色的块（填充到squareImg中，形成questionImagePath)
    public static final String pieceImagePath = workPath + "/slide-piece.jpg";

    public static void main(String[] a){

        int[] imageSize = Util.getImageSize(sourceImgPath);

        int sourceWidth = imageSize[0];
        int sourceHeight = imageSize[1];
        int height, width, x, y;
        if(sourceWidth/sourceHeight>3){
            height = sourceHeight;
            width = 3*height;
        }else{
            width = sourceWidth;
            height = sourceWidth/3;
        }
        x = 0;
        y = 0;


        BufferedImage questionBi = null;
        try {
            questionBi = Util.cutImage(x, y, width, height, sourceImgPath);
            ImageIO.write(questionBi, "jpg", new File(questionRawImagePath)); //保存裁切后的图片
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 区块的坐标
        // 后50% ~ 80%
        int pieceX = (width-blockSize)/2 + new Random().nextInt((width - blockSize)/2 - (width - blockSize)/5);
        // 上20%～80%
        int pieceY = (height-blockSize)/5 + new Random().nextInt((height-blockSize)*2/5);

        // 从裁切后的图片中，挖出区块
        BufferedImage pieceBi;
        BufferedImage grayBi;
        try {
            pieceBi = Util.cutImage(pieceX, pieceY, blockSize, blockSize, questionRawImagePath);
            ImageIO.write(pieceBi, "jpg", new File(pieceImagePath)); //保存区块

            // 区块灰质，然后填充回图片中
            grayBi = Util.grayImage(pieceBi);
            Graphics gb = grayBi.createGraphics();
            gb.setColor(Color.BLACK);
            gb.drawRect(0, 0, blockSize - 1, blockSize - 1);
            gb.drawRect(2, 2, blockSize - 1, blockSize - 1);
            gb.drawRect(0, 0, blockSize - 2, blockSize - 2);
            try {
                Graphics g = questionBi.createGraphics();
                g.drawImage(grayBi, pieceX, pieceY, blockSize, blockSize, null);
                g.dispose();
                ImageIO.write(questionBi, "jpg", new File(questionImagePath));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
