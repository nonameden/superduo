package barqsoft.footballscores.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by nonameden on 28/07/15.
 */
public class ScoreListWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return(new ScoreListWidgetFactory(this.getApplicationContext(), intent));
    }
}
