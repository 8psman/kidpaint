package com.namnv.project.kidpaint;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.facebook.FacebookOperationCanceledException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.namnv.project.kidpaint.adapter.GalleryGridAdapter;
import com.namnv.project.kidpaint.object.PaintHolder;
import com.namnv.project.kidpaint.object.PaintReference;
import com.namnv.project.kidpaint.task.ImageLoaderAsyncTask;
import com.namnv.project.kidpaint.task.PaintListLoaderAsyncTask;
import com.namnv.project.kidpaint.ui.DialogFactory;
import com.namnv.project.kidpaint.ui.Painter;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.List;

public class Gallery extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, AdapterView.OnItemClickListener, SearchView.OnQueryTextListener{

    private static final int REAUTH_ACTIVITY_CODE = 100;

    public static final int PICK_IMAGE_REQUEST_CODE = 1111;

    public static final int NEW_INTENT_CODE = 1357;

    public static final String KEY_NEED_UPDATE_CONTENT = "need_update_content";
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */

    private CharSequence mTitle;
    GalleryGridAdapter adapter;

    boolean isUserClickLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(Application.getInstance().getAppTheme());
        setContentView(R.layout.gallery);

//        mNavigationDrawerFragment = (NavigationDrawerFragment)
//                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
//        mTitle = getTitle();
//
//        // Set up the drawer.mNavigationDrawerFragment
//        .setUp(
//                R.id.navigation_drawer,
//                (DrawerLayout) findViewById(R.id.drawer_layout));

        GridView galleryGridView = (GridView) findViewById(R.id.gallery_grid_view);
        adapter = new GalleryGridAdapter(this, R.layout.gallery_item);
        List<PaintReference> paintRefs = (List<PaintReference>) Application.getInstance().getTempObject(Application.PAINT_REFERENCES);
        for (PaintReference ref : paintRefs)
            adapter.addPaint(ref);
        galleryGridView.setAdapter(adapter);
        galleryGridView.setOnItemClickListener(this);

        Application.getInstance().putTempObject(KEY_NEED_UPDATE_CONTENT, false);

        /**
         * facebook area
         */

        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

//        checkFacebookLoginState();

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
//                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gallery, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    public void onNewPaintFromImage(){
        pickImageByIntent();
    }

    public void pickImageByIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select image"), PICK_IMAGE_REQUEST_CODE);
    }

    public void onImageLoaded(String name, Bitmap bitmap){
        PaintHolder info = new PaintHolder();
        info.name = name;
        info.bitmap = bitmap;
        info.width = bitmap.getWidth();
        info.height = bitmap.getHeight();
        info.background = Color.WHITE;
        Application.getInstance().putTempObject(Painter.KEY_PAINT_INFO, info);
        Intent intent = new Intent(this, Paint.class);
        startActivity(intent);
    }
    public void onSelectedImage(String path){
        final PaintReference ref = new PaintReference();
        ref.name = new File(path).getName();
        ref.path = path;
        final Dialog progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.show();
        new ImageLoaderAsyncTask(ref, new ImageLoaderAsyncTask.OnImageLoadingResultListener() {
            @Override
            public void onResult(Bitmap bitmap) {
                progress.dismiss();
                if (bitmap != null){
                    onImageLoaded(ref.name, bitmap);
                }else{
                    new AlertDialog.Builder(Gallery.this)
                            .setTitle("Lỗi")
                            .setMessage("Không tải được ảnh")
                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).create().show();
                }
            }
        }).execute();
    }

    public void onNewPaint(){
        Intent intent = new Intent(this, New.class);
        startActivityForResult(intent, NEW_INTENT_CODE);
    }
    public void onNewTest(){
        Intent intent = new Intent(this, MultiTouch.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_new_paint:
                onNewPaint();
//                onNewTest();
                break;
            case R.id.action_new_paint_from_image:
                onNewPaintFromImage();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            switch (requestCode){
                case NEW_INTENT_CODE:
                    Intent intent = new Intent(this, Paint.class);
                    startActivity(intent);
                    break;
                case PICK_IMAGE_REQUEST_CODE:
                    Uri uri = data.getData();
                    Cursor cursor = getContentResolver().query(uri,
                            new String[] {MediaStore.Images.ImageColumns.DATA }, null, null, null);
                    cursor.moveToFirst();
                    String imagePath = cursor.getString(0);
                    cursor.close();
                    onSelectedImage(imagePath);
                    break;
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Application.getInstance().putTempObject(Application.PAINT_TO_PREVIEW, adapterView.getItemAtPosition(position));
        Intent intent = new Intent(this, PreviewPaint.class);
        startActivity(intent);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.getFilter().filter(newText);
        return true;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((Gallery) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    public void checkFacebookLoginState(){
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            // Get the user's data
            makeMeRequest(session);
        }
    }

    public void onLoginFacebook(View view){
        isUserClickLogin = true;
        mNavigationDrawerFragment.closeDrawer();
        Session.openActiveSession(this, true, callback);
    }

    public void onLogoutFacebook(View view){
        mNavigationDrawerFragment.closeDrawer();
        AlertDialog alertDialog = DialogFactory.createRequestDialog(this, "Logout", "You want to logout your facebook account?",
                new Runnable() {
                    @Override
                    public void run() {
                        Session session = Session.getActiveSession();
                        if (session != null) {
                            if (!session.isClosed()) {
                                session.closeAndClearTokenInformation();
                            }
                        } else {
                            session = new Session(Gallery.this);
                            Session.setActiveSession(session);
                            session.closeAndClearTokenInformation();
                        }
                        mNavigationDrawerFragment.onUserLogout();
                    }
                });
        alertDialog.show();
    }

    public void onChangeTheme(View view){
        int theme = Application.getInstance().getThemeResourceFromName((String)view.getTag());
        if (theme != Application.getInstance().getAppTheme()){
            mNavigationDrawerFragment.closeDrawer();
            Application.getInstance().setAppTheme(theme);
            restart();
        }
    }

    public void onChangeTheme(int theme){
        if (theme != Application.getInstance().getAppTheme()){
            mNavigationDrawerFragment.closeDrawer();
            Application.getInstance().setAppTheme(theme);
            restart();
        }
    }
    public void restart(){
        finish();
        startActivity(new Intent(this, Gallery.class));
    }


    /**
     * start of facebook section--------------------------------------------------------------------
     */
    private UiLifecycleHelper uiHelper;

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(final Session session, final SessionState state, final Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private void makeMeRequest(final Session session) {
        // Make an API call to get user data and define a
        // new callback to handle the response.
        Request request = Request.newMeRequest(session,
                new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        // If the response is successful
                        if (session == Session.getActiveSession()) {
                            if (user != null) {
                                // Set the id for the ProfilePictureView
                                // view that in turn displays the profile picture.
                                mNavigationDrawerFragment.onUserLogin(user);
                                if (isUserClickLogin){
                                    mNavigationDrawerFragment.showDrawer();
                                    DialogFactory.createMessageDialog(Gallery.this,
                                            "You've just logged in your facebook account! Have fun!")
                                            .show();
                                    isUserClickLogin = false;
                                }
                            }
                        }
                        if (response.getError() != null) {
                            // Handle errors, will do so later.
                            Log.d(Application.TAG, "Error on GraphicUserCallback");
                        }
                    }
                });
        request.executeAsync();
    }

    private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
        if (session != null && session.isOpened()) {
            // Get the user's data.
            makeMeRequest(session);
        }
        if (session == null || (state == SessionState.CLOSED_LOGIN_FAILED)){
            if (exception != null && exception instanceof FacebookOperationCanceledException){
                DialogFactory.createMessageDialog(this, "Cannot login to facebook account!").show();
            }
        }

    }

    /**
     * end of facebook section----------------------------------------------------------------------
     */


    /**
     * activity lifecycle
     */
    @Override
    protected void onResume() {
        super.onResume();
        uiHelper.onResume();

        // check update content
        boolean needUpdateContent = (Boolean) Application.getInstance().getTempObject(KEY_NEED_UPDATE_CONTENT);
        if (needUpdateContent){
            ImageLoader.getInstance().clearMemoryCache();
            new PaintListLoaderAsyncTask(adapter, null).execute();
            Application.getInstance().putTempObject(KEY_NEED_UPDATE_CONTENT, false);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        uiHelper.onSaveInstanceState(bundle);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }
}
