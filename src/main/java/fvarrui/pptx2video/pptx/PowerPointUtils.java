package fvarrui.pptx2video.pptx;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;

public class PowerPointUtils {
	
	public static XMLSlideShow loadSlideShow(String pptxFile) throws IOException {
		FileInputStream is = new FileInputStream(pptxFile);
		XMLSlideShow pptx = new XMLSlideShow(is);
		is.close();
		return pptx;
	}
	
	public static List<String> extractNotes(String pptxFile) throws IOException {
		List<String> notes = new ArrayList<String>();
		XMLSlideShow pptx = loadSlideShow(pptxFile);
		List<XSLFSlide> slides = pptx.getSlides();
		for (int i = 0; i < slides.size(); i++) {
			XSLFSlide slide = slides.get(i);
            List<List<XSLFTextParagraph>> notesList = slide.getNotes().getTextParagraphs();
            for (List<XSLFTextParagraph> paragraphs : notesList) {
            	for (XSLFTextParagraph text : paragraphs) {
            		if (!text.isHeaderOrFooter())
            			notes.add(text.getText());
            	}
            }
		}
		pptx.close();
		return notes;
	}
	
	public static List<File> extractImages(String pptxFile, File destination) throws IOException {
		List<File> images = new ArrayList<>();
		
		XMLSlideShow pptx = loadSlideShow(pptxFile);

		Dimension pgsize = pptx.getPageSize();

		List<XSLFSlide> slides = pptx.getSlides();
		for (int i = 0; i < slides.size(); i++) {

			BufferedImage img = new BufferedImage(pgsize.width, pgsize.height, 1);

			Graphics2D graphics = img.createGraphics();
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

			graphics.setColor(Color.white);
			graphics.clearRect(0, 0, pgsize.width, pgsize.height);
			graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));

			XSLFSlide slide = slides.get(i);
			
			// render
			slide.draw(graphics);

			// save the output
			File pngFile = new File(destination, "slide-" + (i + 1) + ".png");
			System.out.println((i + 1) + ") saving slide to " + pngFile.getName());
			FileOutputStream out = new FileOutputStream(pngFile);
			ImageIO.write(img, "png", out);
			out.close();
			
			images.add(pngFile);
			
		}
		
		pptx.close();
		
		return images;
	}
	
}
