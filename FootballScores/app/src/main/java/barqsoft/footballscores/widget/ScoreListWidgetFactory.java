package barqsoft.footballscores.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;

/**
 * Created by nonameden on 28/07/15.
 */
public class ScoreListWidgetFactory implements RemoteViewsService.RemoteViewsFactory {

    private final Context mContext;
    private Cursor mCursor;

    public ScoreListWidgetFactory(Context applicationContext, Intent intent) {
        mContext = applicationContext;
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return getCursor().getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        getCursor().moveToPosition(position);

        RemoteViews row = new RemoteViews(
                mContext.getPackageName(),
                R.layout.scores_list_item
        );

        String homeTeamName = mCursor.getString(
                mCursor.getColumnIndex(DatabaseContract.scores_table.HOME_COL));
        row.setTextViewText(R.id.home_name, homeTeamName);

        String awayTeamName = mCursor.getString(
                mCursor.getColumnIndex(DatabaseContract.scores_table.AWAY_COL));
        row.setTextViewText(R.id.away_name, awayTeamName);

        row.setTextViewText(R.id.data_textview, mCursor.getString(
                mCursor.getColumnIndex(DatabaseContract.scores_table.DATE_COL)));

        int homeGoals = mCursor.getInt(
          mCursor.getColumnIndex(DatabaseContract.scores_table.HOME_GOALS_COL));
        int awayGoals = mCursor.getInt(
                mCursor.getColumnIndex(DatabaseContract.scores_table.AWAY_GOALS_COL));

        row.setTextViewText(R.id.score_textview,
                Utilies.getScores(homeGoals, awayGoals));
        row.setImageViewResource(R.id.home_crest,
                Utilies.getTeamCrestByTeamName(homeTeamName));
        row.setImageViewResource(R.id.away_crest,
                Utilies.getTeamCrestByTeamName(awayTeamName));

        return row;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        getCursor().moveToPosition(position);
        int columnIndex = getCursor().getColumnIndex(DatabaseContract.scores_table._ID);
        return getCursor().getLong(columnIndex);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private Cursor getCursor() {
        if(mCursor == null) {
            mCursor = mContext.getContentResolver().query(
                    DatabaseContract.BASE_CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );
        }

        return mCursor;
    }
}
