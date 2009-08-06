package freenet.clients.http.updateableelements;

import java.util.Timer;
import java.util.TimerTask;

import freenet.clients.http.SimpleToadletServer;
import freenet.clients.http.ToadletContext;
import freenet.support.Base64;
import freenet.support.HTMLNode;

/** A pushed element that counts up every second. Only for testing purposes. */
public class TesterElement extends BaseUpdateableElement {

	private int		status	= 0;

	private int		maxStatus;

	ToadletContext	ctx;

	Timer			t;

	String			id;

	public TesterElement(ToadletContext ctx, String id, int max) {
		super("div", ctx);
		this.id = id;
		this.ctx = ctx;
		this.maxStatus = max;
		init();
		t = new Timer(true);
		t.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				update();
			}
		}, 0, 1000);
	}

	public void update() {
		status++;
		if (status >= maxStatus) {
			t.cancel();
		}
		((SimpleToadletServer) ctx.getContainer()).pushDataManager.updateElement(getUpdaterId(ctx.getUniqueId()));
	}

	@Override
	public void dispose() {
		t.cancel();
	}

	@Override
	public String getUpdaterId(String requestId) {
		return getId(requestId,id);
	}
	
	public static String getId(String requestId,String id){
		return Base64.encodeStandard(("test:" + requestId + "id:" + id+"gndfjkghghdfukggherugbdfkutg54ibngjkdfgyisdhiterbyjhuyfghdightw7i4tfgsdgo;dfnghsdbfuiyfgfoinfsdbufvwte4785tu4kgjdfnzukfbyfhe48e54gjfdjgbdruserigbfdnvbxdio;fherigtuseofjuodsvbyfhsd8ofghfio;").getBytes());
	}

	@Override
	public String getUpdaterType() {
		return UpdaterConstants.REPLACER_UPDATER;
	}

	@Override
	public void updateState(boolean initial) {
		children.clear();
		addChild(new HTMLNode("div", "" + status));
	}

}
