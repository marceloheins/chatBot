package com.marcelo.chatBot.service;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;


@Service
public class SanitizationService {
    public String sanitizeUserText(String raw){
        if (raw == null) return "";

        String cleaned = Jsoup.clean(raw, Safelist.none());
        cleaned = cleaned.replaceAll("\\s+", " ").trim();
        return cleaned;

    }

    public boolean isLikelyPromptInjection(String text){
        if (text == null) return false;
        String t =  text.toLowerCase();
        return t.contains("ignore previous") || t.contains("ignore all previous") ||
               t.contains("system prompt") || t.contains("disregard instructions") ||
               t.contains("forget previous") || t.contains("jailbreak");
               
    }
    
}
