package action.notes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar toolbar;
    String data = "";
    String newData = "";
    EditText textWindow;
    String storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textWindow = (EditText) findViewById(R.id.notepad_input);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = toolbar.getTitle().toString();
                newData = textWindow.getText().toString();
                if (newData.equals(data) | newData.equals("")) {
                    textWindow.setText("");
                    toolbar.setTitle("Notes");
                    data = newData;
                } else {
                    saveUnsavedChanges(title, newData);
                    data = newData;
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_save) {
            String title = toolbar.getTitle().toString();
            data = textWindow.getText().toString();
            if (!title.equals("Notes")) {
                saveToFileWithoutAlertDialog(title, data);
            } else {
                saveToFile(data);
            }
        } else if (id == R.id.nav_saveAs) {
            data = textWindow.getText().toString();
            saveToFile(data);
        } else if (id == R.id.nav_new) {
            String title = toolbar.getTitle().toString();
            newData = textWindow.getText().toString();
            if (newData.equals(data) | newData.equals("")) {
                textWindow.setText("");
                toolbar.setTitle("Notes");
                data = newData;
            } else {
                saveUnsavedChanges(title, newData);
            }
        } else if (id == R.id.nav_open) {
            String title = toolbar.getTitle().toString();
            newData = textWindow.getText().toString();
            if (newData.equals(data) | newData.equals("")) {
                openFile();
            } else {
                saveUnsavedChanges(title, newData);
            }
        } else if (id == R.id.nav_about) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("App Developed by Vikram Singh\n\n" +
                    "Gurukula Kangri University\n\n" + "singh.vikram.0714@gmail.com");
            builder.setCancelable(true);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else if (id == R.id.nav_exit) {
            String title = toolbar.getTitle().toString();
            newData = textWindow.getText().toString();
            if (newData.equals(data) | newData.equals("")) {
                finish();
                System.exit(0);
            } else {
                saveUnsavedChanges(title, newData);
            }
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void saveToFile(final String writeData) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("File Name");
        builder.setCancelable(false);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_input, null);
        builder.setView(dialogView);
        final EditText editText = (EditText) dialogView.findViewById(R.id.input);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String fileName = editText.getText().toString();
                try {
                    File newFile;
                    newFile = new File(System.getenv("EXTERNAL_STORAGE") + "/" + fileName);
                    newFile.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(newFile);
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
                    outputStreamWriter.append(writeData);
                    outputStreamWriter.close();
                    fileOutputStream.close();
                    toolbar.setTitle(fileName);
                    Toast.makeText(getApplicationContext(), "File Saved", Toast.LENGTH_SHORT).show();
                    data = newData;
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "File Not Saved", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }

        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                data = "";
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void saveToFileWithoutAlertDialog(String title, String writeData) {
        try {
            File newFile;
            //if (storage.equals("Internal Storage")) {
            newFile = new File(System.getenv("EXTERNAL_STORAGE") + "/" + title);
            //} else {
            //newFile = new File(System.getenv("SECONDARY_STORAGE") + "/" + title);
            //}
            newFile.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(newFile);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.append(writeData);
            outputStreamWriter.close();
            fileOutputStream.close();
            toolbar.setTitle(title);
            Toast.makeText(getApplicationContext(), "File Saved", Toast.LENGTH_SHORT).show();
            data = newData;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "File Not Saved", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveUnsavedChanges(final String title, final String writeData) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("File has Unsaved Changes!");
        builder.setCancelable(false);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (title.equals("Notes")) {
                    saveToFile(writeData);
                } else {
                    saveToFileWithoutAlertDialog(title, writeData);
                }
            }

        });
        builder.setNegativeButton("Don't Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                EditText editText = (EditText) findViewById(R.id.notepad_input);
                editText.setText("");
                toolbar.setTitle("Notes");
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void openFile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("File Name");
        builder.setCancelable(false);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_input, null);
        builder.setView(dialogView);
        final EditText editText = (EditText) dialogView.findViewById(R.id.input);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String fileName = editText.getText().toString();
                String aDataRow = "";
                String aBuffer = "";
                try {
                    File myFile;
                    //if (storage.equals("Internal Storage")) {
                    myFile = new File(System.getenv("EXTERNAL_STORAGE") + "/" + fileName);
                    //} else {
                    //  myFile = new File(System.getenv("SECONDARY_STORAGE") + "/" + fileName);
                    //}
                    FileInputStream fileInputStream = new FileInputStream(myFile);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                    while ((aDataRow = bufferedReader.readLine()) != null) {
                        aBuffer += aDataRow + "\n";
                    }
                    bufferedReader.close();
                    textWindow.setText(aBuffer);
                    toolbar.setTitle(fileName);
                    data = textWindow.getText().toString();
                    Toast.makeText(getApplicationContext(), "File Opened", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "File Not Found", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
