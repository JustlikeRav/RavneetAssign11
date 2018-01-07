package ravneet.singh2.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ravneet.singh2.R;

//Ravneet Singh
//n01148757
//Assignment 2

public class RavDown extends Fragment {
    private Button downloadbtn;
    private ImageView mImageView;
    private View mView;
    private String image_url = "http://s1.thingpic.com/images/FZ/BNT6TL6c2YdMp9kHeARDkxNj.png";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.rav_down, container, false);

        downloadbtn = mView.findViewById(R.id.download_btn);
        mImageView = mView.findViewById(R.id.downloaded_image);

        String path = getActivity().getFilesDir().getAbsolutePath() + "/ravDir/downloaded_image.jpg";
        File mFile = new File(path);
        if(mFile.exists()){
            Toast.makeText(getActivity(), "File already exists", Toast.LENGTH_SHORT).show();
            mImageView.setImageDrawable(Drawable.createFromPath(path));
        }

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        downloadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ((Build.VERSION.SDK_INT >= 23) && checkPermission()) {
                    String path = getActivity().getFilesDir().getAbsolutePath() + "/ravDir/downloaded_image.jpg";
                    File mFile = new File(path);
                    if(!mFile.exists()){
                        DownloadTask downloadTask = new DownloadTask();
                        downloadTask.execute(image_url);
                    } else {
                        Toast.makeText(getActivity(), "File already exists", Toast.LENGTH_SHORT).show();
                        mImageView.setImageDrawable(Drawable.createFromPath(path));
                    }
                } else {
                    requestPermission();
                }
            }
        });
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);


        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(getActivity(),
                            "Permission accepted", Toast.LENGTH_LONG).show();
                    DownloadTask downloadTask = new DownloadTask();
                    downloadTask.execute(image_url);

                } else {
                    Toast.makeText(getActivity(),
                            "Permission denied", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }

    class DownloadTask extends AsyncTask<String, Integer, String> {

        ProgressDialog progressDialog;

        /**
         * Set up a ProgressDialog
         */
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Download in progress...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(100);
            progressDialog.setProgress(0);
            progressDialog.show();

        }

        /**
         *  Background task
         */
        @Override
        protected String doInBackground(String... params) {
            String path = params[0];
            int file_length;

            Log.i("Info: path", path);
            try {
                URL url = new URL(path);

                HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                HttpURLConnection.setFollowRedirects(false);
                huc.setConnectTimeout(15 * 1000);
                huc.setRequestMethod("GET");
                huc.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");
                huc.connect();
                file_length = huc.getContentLength();

                /**
                 * Create a folder
                 */
                File new_folder = new File(getActivity().getFilesDir(),"ravDir");
                if (!new_folder.exists()) {
                    if (new_folder.mkdir()) {
                        Log.i("Info", "Folder succesfully created");
                    } else {
                        Log.i("Info", "Failed to create folder");
                    }
                } else {
                    Log.i("Info", "Folder already exists");
                }

                /**
                 * Create an output file to store the image for download
                 */
                File output_file = new File(new_folder, "downloaded_image.jpg");
                OutputStream outputStream = new FileOutputStream(output_file);

                InputStream inputStream = new BufferedInputStream(url.openStream(), 8192);
                byte [] data = new byte[1024];
                int total = 0;
                int count;
                while ((count = inputStream.read(data)) != -1) {
                    total += count;

                    outputStream.write(data, 0, count);
                    int progress = 100 * total / file_length;
                    publishProgress(progress);

                    Log.i("Info", "Progress: " + Integer.toString(progress));
                }
                inputStream.close();
                outputStream.close();

                Log.i("Info", "file_length: " + Integer.toString(file_length));

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Download complete.";
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.hide();
            Toast.makeText(getActivity(), result, Toast.LENGTH_LONG).show();
            File folder = new File(getActivity().getFilesDir(),"ravDir");
            File output_file = new File(folder, "downloaded_image.jpg");
            String path = output_file.toString();
            mImageView.setImageDrawable(Drawable.createFromPath(path));
            Log.i("Info", "Path: " + path);
        }
    }
}
