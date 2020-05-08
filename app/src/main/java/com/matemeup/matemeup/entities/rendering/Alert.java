package com.matemeup.matemeup.entities.rendering;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.matemeup.matemeup.R;

import java.util.List;

public class Alert {
    public static void yesNo(Context context, String title, String message, final AlertCallback cb) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        dialog.setTitle(title)
                .setMessage(message)
                .setNegativeButton(context.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        dialoginterface.cancel();
                        cb.fail();
                    }})
                .setPositiveButton(context.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        dialoginterface.cancel();
                        cb.success();
                    }
                }).show();
    }
    public static void ok(Context context, String title, List<String> message, final AlertCallback cb) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        dialog.setTitle(title);

        for (int i = 0; i < message.size(); i++) {
            dialog.setMessage(message.get(i));
        }
        dialog.setNeutralButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        dialoginterface.cancel();
                        cb.success();
                    }}).show();
    }


    public static void ok(Context context, String title, String message, final AlertCallback cb) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        dialog.setTitle(title)
                .setMessage(message)
                .setNeutralButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        dialoginterface.cancel();
                        cb.success();
                    }}).show();
    }
}
