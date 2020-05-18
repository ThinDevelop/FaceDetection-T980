package com.lightweh.facedetection;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.common.pos.api.util.PosUtil;

import static android.nfc.NfcAdapter.FLAG_READER_NFC_A;
import static android.nfc.NfcAdapter.FLAG_READER_NFC_F;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION = 1;

    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NfcManager mNfcManager = (NfcManager) getSystemService(Context.NFC_SERVICE);
        mNfcAdapter = mNfcManager.getDefaultAdapter();
        Log.e("panya", "isEnabled : " + mNfcAdapter.isEnabled());
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (hasPermission()) {
            if (null == savedInstanceState) {
                setFragment();
            }
        } else {
            requestPermission();
        }
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()), 0);
        init_NFC();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setFragment();
            } else {
                requestPermission();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                Toast.makeText(MainActivity.this, "Camera permission are required for this demo", Toast.LENGTH_LONG).show();
            }
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION);
        }
    }

    private void setFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, CameraFragment.newInstance())
                .commit();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mNfcAdapter != null) {
            mNfcAdapter.enableReaderMode(this, new NfcAdapter.ReaderCallback() {
                @Override
                public void onTagDiscovered(Tag tag) {
                    byte[] ID = tag.getId();
                    String UID = bytesToHexString(ID);
                    String IDString = bytearray2Str(hexStringToBytes(UID.substring(2, UID.length())), 0, 4, 10);
                    if ("0629076207".equals(IDString)) {
                        PosUtil.setRelayPower(1);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        PosUtil.setRelayPower(0);
                                    }
                                }, 5000);
                            }
                        });

                    }
                    Log.e("panya", "IDString : " + IDString);
                }
            }, FLAG_READER_NFC_A, null);//ForegroundDispatch(this, mPendingIntent, null, null);
        }
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("0x");
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            stringBuilder.append(buffer);
        }
        return stringBuilder.toString();
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mNfcAdapter != null) {
            stopNFC_Listener();
        }
    }

    private void init_NFC() {
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
    }

    private void stopNFC_Listener() {
        mNfcAdapter.disableForegroundDispatch(this);
    }


    private static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    private static String bytearray2Str(byte[] data, int start, int length, int targetLength) {
        long number = 0;
        if (data.length < start + length) {
            return "";
        }
        for (int i = 1; i <= length; i++) {
            number *= 0x100;
            number += (data[start + length - i] & 0xFF);
        }
        return String.format("%0" + targetLength + "d", number);
    }
}