package org.instedd.cdx.sync.watcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class PathWatcherIntegrationTest {

	@Rule
	public TemporaryFolder root = new TemporaryFolder();
	private AtomicInteger singleChangeEventsCount = new AtomicInteger(0);
	private AtomicInteger globalChangeEventsCount = new AtomicInteger(0);
	private File rootDir;
	private Runnable watch;

	@Before
	public void setup() {
		rootDir = root.getRoot();
		watch = createWatcher(rootDir);
	}

	@Test
	public void notifiesListenerWhenFileSystemChanges() throws Exception {
		Thread thread = new Thread(watch);
		thread.start();

		touch("foo");
		touch("bar");
		touch("baz");

		Thread.sleep(60000);
		thread.interrupt();
		thread.join();

		assertTrue(globalChangeEventsCount.get() >= 1);
		assertEquals(3, singleChangeEventsCount.get());
	}

	private void touch(String name) throws IOException {
	  FileUtils.touch(new File(rootDir, name));
  }

	private Runnable createWatcher(File rootDir) {
	  return PathWatcher.asyncWatch(rootDir.toPath(), new PathWatchListener() {
			public void onSinglePathChange(Kind<Path> kind, Path path) {
				singleChangeEventsCount.incrementAndGet();
			}

			public void onGlobalPathChange(Path path) {
				globalChangeEventsCount.incrementAndGet();
			}
		});
  }

}