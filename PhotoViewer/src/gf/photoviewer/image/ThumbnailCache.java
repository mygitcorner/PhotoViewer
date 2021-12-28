package gf.photoviewer.image;

import static gf.photoviewer.PhotoViewerConstants.THUMBNAIL_SIZE;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.logging.Level;

import gf.photoviewer.PVLogger;
import gf.photoviewer.event.ImageEvent;
import gf.photoviewer.event.PVHandler;
import gf.photoviewer.resources.Picture;
import net.coobird.thumbnailator.Thumbnails;

public class ThumbnailCache {
	private static final int WORKER_SIZE = 5;
	
	private static ThumbnailCache singleton = null;
	
	private ConcurrentHashMap<Picture, BufferedImage> imageMap;
	private PVHandler<ImageEvent> imageHandler;
	
	private BlockingQueue<Picture> pictureQueue;
	private Lock queueLock;
	
	public static ThumbnailCache getInstance() {
		if (singleton == null)
			singleton = new ThumbnailCache();
		
		return singleton;
	}
	
	private ThumbnailCache() {
		imageMap = new ConcurrentHashMap<>();
		imageHandler = new PVHandler<>();
		pictureQueue = new ArrayBlockingQueue<>(1);
		queueLock = new ReentrantLock();
		
		for (int i = 0; i < WORKER_SIZE; i++)
			new PictureProcessingThread().start();
	}
	
	public void cache(List<Picture> pictures) {
		if (!pictures.isEmpty()) {
			synchronized (queueLock) {
				pictureQueue = new ArrayBlockingQueue<>(pictures.size());

				for (Picture picture : pictures) {
					if (!isCached(picture))
						pictureQueue.add(picture);
				}
				
				queueLock.notifyAll();
			}
		} else {
			pictureQueue.clear();
		}	
	}
	
	private BlockingQueue<Picture> getPictureQueue() {
		return pictureQueue;
	}
	
	public BufferedImage get(Picture picture) {
		return imageMap.get(picture);
	}
	
	public boolean isCached(Picture picture) {
		return get(picture) != null;
	}
	
	public void addImageEventListener(Consumer<ImageEvent> c) {
		imageHandler.addListener(c);
	}
	
	private class PictureProcessingThread extends Thread { 
		public PictureProcessingThread() {
			setDaemon(true);
		}
		
		@Override
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				Picture picture;
				
				try {
					synchronized (queueLock) {
						while (getPictureQueue().isEmpty()) {
							queueLock.wait();
						}
						
						picture = getPictureQueue().poll();
					}
				} catch (InterruptedException e) {
					break;
				}
				
				try {
					BufferedImage image = Thumbnails
							.of(picture.getPath().toFile())
							.size(THUMBNAIL_SIZE, THUMBNAIL_SIZE)
							.asBufferedImage();

					imageMap.put(picture, image);
					imageHandler.invoke(new ImageEvent(picture));
				} catch (FileNotFoundException e) {
				} catch (IOException e) {
					PVLogger.getLogger().log(Level.WARNING, "Processing "
							+ picture + "\n" + e.getMessage(), e);
				}
			}
		}
	}
}