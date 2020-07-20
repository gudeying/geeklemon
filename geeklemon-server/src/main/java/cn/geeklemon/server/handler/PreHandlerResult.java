package cn.geeklemon.server.handler;

import cn.geeklemon.server.viewrender.ModelAndView;

public class PreHandlerResult {
	/**
	 * true交给下一个preHandler，如果是最后一个，将会交给pipeline的下一个Handler
	 * false:將根据ModelAndView返回相应的结果，不会继续传下去。类似于response.write()且不调用filterChain继续执行
	 */
	private boolean fireNext;
	private ModelAndView modelAndView;

	public boolean isFireNext() {
		return fireNext;
	}

	public void setFireNext(boolean fireNext) {
		this.fireNext = fireNext;
	}

	public ModelAndView getModelAndView() {
		return modelAndView;
	}

	public void setModelAndView(ModelAndView modelAndView) {
		this.modelAndView = modelAndView;
	}

}
