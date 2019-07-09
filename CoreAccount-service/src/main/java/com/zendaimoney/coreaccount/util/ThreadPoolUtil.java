package com.zendaimoney.coreaccount.util;

import com.zendaimoney.exception.BusinessException;

import java.util.Collection;
import java.util.concurrent.*;

/**
 * 线程池工具类
 * 
 * @author binliu
 * @since 1.0
 */
public abstract class ThreadPoolUtil {

	public static class ThreadPool<V> extends ExecutorCompletionService<V> {
		private Executor executor;

		public ThreadPool(Executor executor) {
			super(executor);
			this.executor = executor;
		}

		public ExecutorService getThreadPool() {
			if (this.executor instanceof ExecutorService) {
				return (ExecutorService) executor;
			}
			throw new UnsupportedOperationException("executor must be " + ExecutorService.class.getName());// 必须传入ExecutorService
		}
	}

	/**
	 * 带缓存功能的线程池默认池的大小为无限大
	 * 
	 * @return (线程池)
	 */
	static public ExecutorService newCachedPool() {
		return Executors.newCachedThreadPool();
	}

	/**
	 * 限制池大小的带缓存功能的线程池(不同于newFixedPool方法返回的线程池)
	 * 
	 * @param nThreads
	 * @return (线程池)
	 */
	static public ExecutorService newCachedPool(int nThreads) {
		return new ThreadPoolExecutor(0, nThreads, 30L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
	}

	/**
	 * 默认使用CachedExecutorService构造CompletionService
	 * 
	 * @return ThreadPool
	 */
	static public <V> ThreadPool<V> newCompletionService() {
		return new ThreadPool<V>(newCachedPool());
	}

	/**
	 * 默认使用FixedThreadPool构造CompletionService
	 * 
	 * @return ThreadPool
	 */
	static public <V> ThreadPool<V> newCompletionService(int nThreads) {
		return new ThreadPool<V>(newCachedPool(nThreads));
	}

	/**
	 * 根据客户端提供的ExecutorService构造CompletionService
	 * 
	 * @param executorService
	 * @return
	 */
	static public <V> ThreadPool<V> newCompletionService(ExecutorService executorService) {
		return new ThreadPool<V>(executorService);
	}

	/**
	 * 提交任务到线程池
	 * 
	 * @param completionService
	 * @param task
	 */
	static public <V> void submitTask(CompletionService<V> completionService, Callable<V> task) {
		completionService.submit(task);
	}

	/**
	 * 批量提交任务
	 * 
	 * @param executorService
	 * @param tasks
	 */
	static public <V> void submitTask(ExecutorService executorService, Collection<Callable<V>> tasks) {
		try {
			executorService.invokeAll(tasks);
		} catch (InterruptedException e) {
			shutNow(executorService);
			throw new BusinessException(e);
		}
	}

	/**
	 * 获取任务的处理结果
	 * 
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	static public <V> V getResult(CompletionService<V> completionService) throws InterruptedException, ExecutionException {
		return completionService.take().get();
	}

	/**
	 * 立刻关闭线程池释放系统资源
	 */
	static public void shutNow(Executor executor) {
		if (executor instanceof ThreadPoolExecutor) {
			ThreadPoolExecutor poolExecutor = (ThreadPoolExecutor) executor;
			while (!poolExecutor.isShutdown()) {
				poolExecutor.shutdownNow();
			}
		}
	}

	/**
	 * 关闭线程池禁止向线程池提交新的任务
	 * 
	 * @param threadPool
	 *            (线程池)
	 */
	static public void shut(ExecutorService threadPool) {
		if (threadPool != null)
			threadPool.shutdown();
	}
}
