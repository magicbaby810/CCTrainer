
## CCTrainer

#### 目的：尽可能帮助更多改造原有项目和新手接入CC，产生的各种困惑

1. 接入成本是否高
2. 接入后网络框架是否可抽离
3. 接入后消息传递是否可解耦
4. 接入后三方库怎么做到抽离、共用
5. 接入后杂乱的资源是否可分割
6. 接入后是否会增加编译耗时
7. 接入后各moudle解耦出来，application怎么划分

#### 一、接入成本不高

CC的文档也是极尽详细，大家按着文档里的步骤一步一步来就行了。

- 这里我的一个建议是，提前把自己的业务模块划分好，确定哪几个是需要分离，哪些是不确定的。需要分离的moudle可以直接接入CC，不确定的也是可以先放着不动，而不像其他的组件化方案一样，需要对项目整体进行路由改造，这就是billy大佬强调的可渐进式改造。

#### 二、网络框架可抽离

现在主流的网络框架是rxjava+retrofit+okhttp，同时结合mvp模式，可以在界面里把一个网络请求做到极简化。组件化后，网络框架该怎么处理呢？怎么避免接口实现类里动辄上百个接口和一堆分不清是否重复的数据类？怎么优雅避免网络请求带来的内存泄漏问题？下面就探讨下这些问题。（具体详细用例请看CCTrainer项目）

- 每个组件都要使用网络框架请求数据，请求的主体是不变的，而每一个请求的接口实现和返回数据是不同的，那我们就把网络框架骨干下沉到公共库里，接口实现会在各模块独自创建，当然数据类也会写在各自模块内，这样就避免了网络框架的接口和数据类的混乱。至于怎么写，大家可以参考下CCTrainer里的实现方式，把网络框架下沉到`demo_common_base`公共库里，里面封装retrofit请求单例`Http`，和泛型静态方法`getApiService`，通过这个泛型方法传入不同模块的请求接口类，实现各模块的接口类分离，对应的返回数据类也就在各自模块创建，再也不会混乱不堪。  

	调用例子如下

	```
	@Override
    public Observable<Response<User>> login(String code) {

        return Http.getApiService(LoginApiService.class).login(code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
	    }
	}
	```
	
	```
	public static <T> T getApiService(Class<T> tClass) {
	        return getRetrofit().create(tClass);
	}
	```  
	  
	  


- 网络请求的内存泄漏问题，处理起来也很麻烦，记得以前最开始的时候需要引入`rxlifecycler`库，然后要把基类继承的`activity`和`fragment`类替换成`rxactivity`和`rxfragment`，在每个请求处理上加上`.compose(this.bindUntilEvent(ActivityEvent.XXX))`，这样处理起来并不是很赞。在CCTrainer里，会让你感觉不到处理的存在。CCTrainer里封装了一整套的mvp模式，mvp模式实现网络请求处理和返回数据的刷新操作，大家应该都很熟悉。通过泛型，在每个界面指定`presenter`、`model`和实现`view`接口。而网络请求里通过订阅观察者把接口返回的数据刷新到界面上，我们可以自定义一个观察者类，在类里通过抽象方法`getDisposable`向子类暴露当前订阅事件`disposable`，然后在`presenter`类里通过`basepresenter`里的`rxmanager`类持有当前订阅事件`disposable`，而`rxmanager`类是在`basepresenter`里初始化，并在`onDestroy`方法里取消全部的订阅事件，避免内存泄漏。
 
	```
	public void onDestroy() {
	    Log.e("presenter", mContext + "-->>" + mRxManager);
	    mRxManager.clear();
	}
	```
	而`onDestroy`在什么时候调用呢？答案肯定是在`baseactivity`的`onDestroy`里通过`mPresenter`去调用。

	```
	@Override
    	protected void onDestroy() {
		super.onDestroy();

		if (mPresenter != null) {
		    mPresenter.onDestroy();
	}
    	```
	
	这样等于在调用网络请求的同时，把订阅事件都给管理起来，在界面销毁的时候去取消这些订阅事件。


