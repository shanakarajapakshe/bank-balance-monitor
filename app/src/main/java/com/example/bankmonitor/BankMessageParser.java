package com.example.bankmonitor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class BankMessageParser {
    static TransactionRecord parse(String sender,String raw,long receivedAt){
        if(sender==null||raw==null)return null; String s=sender.trim(),t=raw.trim(); TransactionRecord r=null;
        if(s.equalsIgnoreCase("SAMPATHTXN"))r=sampath(t);
        else if(s.equalsIgnoreCase("Seylan Bank"))r=seylan(t);
        else if(s.equalsIgnoreCase("COMBANK"))r=commercial(t,receivedAt);
        if(r!=null){r.sender=s;r.raw=t;r.receivedAt=receivedAt;r.id=hash(s+"|"+t);r.category=category(r.description,r.type,r.bank);}
        return r;
    }
    private static TransactionRecord sampath(String t){
        Matcher m=p("LKR\\s*([\\d,]+\\.\\d{2})\\s+(debited from|credited to)\\s+AC\\s+\\*\\*(\\d+)\\s*(?:for\\s+)?([\\s\\S]*?)\\s+(\\d{2}/\\d{2}/\\d{4})\\s+(\\d{2}:\\d{2}:\\d{2})").matcher(t);if(!m.find())return null;
        return make("Sampath","AC-"+m.group(3),m.group(2).toLowerCase().startsWith("debited")?"Debit":"Credit",money(m.group(1)),date(m.group(5)+" "+m.group(6),"dd/MM/yyyy HH:mm:ss"),m.group(4),0);
    }
    private static TransactionRecord seylan(String t){
        Matcher m=p("Seylan Card\\s+\\.{3}(\\d+)\\s+debit Txn\\s+(\\d+)\\s+of LKR\\s+([\\d,]+\\.\\d{2})\\s+done on\\s+(\\d{2}/\\d{2}/\\d{4}\\s+\\d{2}:\\d{2}:\\d{2}\\s+(?:AM|PM))\\s+at\\s+([\\s\\S]*?)\\.\\s*Avl bal\\s+([\\d,]+\\.\\d{2})").matcher(t);
        if(m.find())return make("Seylan","CARD-"+m.group(1),"Debit",money(m.group(3)),date(m.group(4),"dd/MM/yyyy hh:mm:ss a"),m.group(5),money(m.group(6)));
        m=p("Your Account\\s+([\\d*]+)\\s+was\\s+(credited|debited)\\s+by LKR\\s+([\\d,]+\\.\\d{2})\\s+([\\s\\S]*?)\\s+on\\s+(\\d{2}/\\d{2}/\\d{4}\\s+\\d{2}:\\d{2}:\\d{2}\\s+(?:AM|PM))\\.\\s*Avl bal\\s+([\\d,]+\\.\\d{2})").matcher(t);if(!m.find())return null;
        String v=m.group(1).replace("*","");return make("Seylan","AC-"+v.substring(Math.max(0,v.length()-4)),cap(m.group(2)),money(m.group(3)),date(m.group(5),"dd/MM/yyyy hh:mm:ss a"),m.group(4),money(m.group(6)));
    }
    private static TransactionRecord commercial(String t,long received){
        Matcher m=p("Purchase at\\s+([\\s\\S]*?)\\s+for LKR\\s+([\\d,]+\\.\\d{2})\\s+on\\s+(\\d{2}/\\d{2}/\\d{2}\\s+\\d{2}:\\d{2}\\s+(?:AM|PM)).*card ending\\s+#(\\d+)").matcher(t);
        if(m.find())return make("Commercial","CARD-"+m.group(4),"Debit",money(m.group(2)),date(m.group(3),"dd/MM/yy hh:mm a"),m.group(1),0);
        m=p("Withdrawal at\\s+([\\s\\S]*?)\\s+for LKR\\s+([\\d,]+\\.\\d{2})\\s+on\\s+(\\d{2}/\\d{2}/\\d{2}\\s+\\d{2}:\\d{2}\\s+(?:AM|PM))\\s+from card ending\\s+#(\\d+)").matcher(t);
        if(m.find())return make("Commercial","CARD-"+m.group(4),"Debit",money(m.group(2)),date(m.group(3),"dd/MM/yy hh:mm a"),"ATM Withdrawal - "+m.group(1),0);
        m=p("Credit for Rs\\.\\s*([\\d,]+\\.\\d{2})\\s+to\\s+(\\d+)\\s+at\\s+(\\d{2}:\\d{2})\\s+at\\s+([\\s\\S]+)").matcher(t);if(!m.find())return null;
        Calendar c=Calendar.getInstance();c.setTimeInMillis(received);String[] hm=m.group(3).split(":");c.set(Calendar.HOUR_OF_DAY,Integer.parseInt(hm[0]));c.set(Calendar.MINUTE,Integer.parseInt(hm[1]));c.set(Calendar.SECOND,0);
        String ac=m.group(2);return make("Commercial","AC-"+ac.substring(ac.length()-4),"Credit",money(m.group(1)),c.getTimeInMillis(),m.group(4),0);
    }
    private static TransactionRecord make(String bank,String account,String type,double amount,long date,String desc,double bal){TransactionRecord r=new TransactionRecord();r.bank=bank;if("Commercial".equals(bank))r.account=(account!=null&&account.toUpperCase(Locale.US).contains("CARD"))?"Commercial - Card":"Commercial - Saving";else if("Sampath".equals(bank))r.account="Sampath";else if("Seylan".equals(bank))r.account="Seylan";else r.account=account;r.type=type;r.amount=amount;r.date=date;r.description=desc.replaceAll("\\s+"," ").replaceAll("\\s+-\\s*$","").trim();r.balance=bal;return r;}
    private static String category(String d,String type,String bank){String s=d.toLowerCase();if("Credit".equals(type)){if("Commercial".equals(bank)&&s.contains("digital banking division"))return "Internal Transfer";return "Income / Deposit";}if(s.matches(".*(own account|internal transfer|fund transfer|ceft|slips|vishwa transfer).*"))return "Internal Transfer";if(s.matches(".*(fuel|filling).*"))return "Fuel";if(s.matches(".*(spar|keells|supermarket|food|restaurant|cafe|hotel).*"))return "Food & Groceries";if(s.matches(".*(dialog|mobitel|telecom|airtel|hutch).*"))return "Phone & Internet";if(s.matches(".*(pharma|hospital|medical|doctor).*"))return "Health";if(s.matches(".*(book|computer society|name-cheap|namecheap|ecom|course).*"))return "Education & Online";if(s.matches(".*(fee|charge).*"))return "Bank Charges";if(s.matches(".*(withdrawal|cash).*"))return "Cash Withdrawal";if(s.matches(".*(uber|pickme|transport|bus|rail).*"))return "Transport";return "Other";}
    private static Pattern p(String s){return Pattern.compile(s,Pattern.CASE_INSENSITIVE);}
    private static double money(String s){return Double.parseDouble(s.replace(",",""));}
    private static long date(String s,String f){try{return new SimpleDateFormat(f,Locale.US).parse(s).getTime();}catch(Exception e){return System.currentTimeMillis();}}
    private static String cap(String s){return s.substring(0,1).toUpperCase()+s.substring(1).toLowerCase();}
    private static String hash(String s){try{byte[] b=MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8));StringBuilder x=new StringBuilder();for(byte q:b)x.append(String.format("%02x",q));return x.toString();}catch(Exception e){return String.valueOf(s.hashCode());}}
    private BankMessageParser(){}
}
