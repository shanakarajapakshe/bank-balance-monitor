package com.example.bankmonitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class BankSmsReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        if (!"android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) return;
        Bundle extras = intent.getExtras();
        if (extras == null) return;
        Object[] pdus = (Object[]) extras.get("pdus");
        if (pdus == null || pdus.length == 0) return;
        String format = extras.getString("format");
        StringBuilder body = new StringBuilder(); String sender = null; long time = System.currentTimeMillis();
        for (Object pdu : pdus) {
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu, format);
            if (sender == null) sender = sms.getDisplayOriginatingAddress();
            body.append(sms.getMessageBody()); time = sms.getTimestampMillis();
        }
        if (!isAcceptedSender(sender)) return;
        TransactionRecord record=BankMessageParser.parse(sender,body.toString(),time);
        if(record!=null)new TransactionDb(context.getApplicationContext()).insert(record);
    }

    static boolean isAcceptedSender(String sender) {
        if (sender == null) return false;
        String s = sender.trim();
        return s.equalsIgnoreCase("COMBANK") || s.equalsIgnoreCase("Seylan Bank") || s.equalsIgnoreCase("SAMPATHTXN");
    }
}
