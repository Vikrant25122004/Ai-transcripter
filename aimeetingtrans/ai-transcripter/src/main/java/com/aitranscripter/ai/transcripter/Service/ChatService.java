package com.aitranscripter.ai.transcripter.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.aitranscripter.ai.transcripter.Entity.messages;
import com.aitranscripter.ai.transcripter.Entity.payload;
import com.aitranscripter.ai.transcripter.Entity.user;
import com.aitranscripter.ai.transcripter.Repository.UserRepository;
import com.aitranscripter.ai.transcripter.Repository.messagesrepo;

@Service
public class ChatService {
    

    @Autowired
    private messagesrepo messagesrepo;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private UserRepository userRepository;
    
    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String API_KEY = "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"; // replace with your key



    public String sendprompt(String id, String prompt, String model ,String email) {
        Optional<messages> chatOpt = messagesrepo.findById(id);
        user user = userRepository.findByemail(email);

        // ------------------- CASE 1: Create new chat -------------------
        if (!chatOpt.isPresent()) {
            // ---- Headers ----
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(API_KEY);

            // ---- Messages in API format ----
            List<Map<String, String>> requestMessages = new ArrayList<>();
            requestMessages.add(Map.of("role", "assistant", "content", ""));
            requestMessages.add(Map.of("role", "user", "content", prompt));

            // ---- Request Body ----
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model",model);
            requestBody.put("temperature", 1);
            requestBody.put("max_completion_tokens", 1024);
            requestBody.put("top_p", 1);
            requestBody.put("stream", false);
            requestBody.put("stop", null);
            requestBody.put("messages", requestMessages);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // ---- API Call ----
            ResponseEntity<String> response = restTemplate.exchange(
                    API_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // ---- Save in DB ----
            messages newChat = new messages();
            payload payload = new payload();
            payload.setRole("user");
            payload.setContent(prompt);
            payload.setResponse(response.getBody());

            ArrayList<payload> payloads = new ArrayList<>();
            payloads.add(payload);
            newChat.setMessages(payloads);

            messagesrepo.save(newChat);

            ArrayList<messages> userChats = user.getMessages();
            if (userChats == null) userChats = new ArrayList<>();
            userChats.add(newChat);
            user.setMessages(userChats);
            userRepository.save(user);

            return response.getBody();
        }

        // ------------------- CASE 2: Append to existing chat -------------------
        else {
            messages chat = chatOpt.get();

            // ---- Headers ----
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(API_KEY);

            // ---- Update chat ----
            chat.setModel(model);
            payload newPayload = new payload();
            newPayload.setRole("user");
            newPayload.setContent(prompt);

            ArrayList<payload> payloads = chat.getMessages();
            if (payloads == null) payloads = new ArrayList<>();
            payloads.add(newPayload);
            chat.setMessages(payloads);

            // ---- Build API Messages ----
            List<Map<String, String>> requestMessages = new ArrayList<>();
            for (payload p : payloads) {
                requestMessages.add(Map.of(
                        "role", p.getRole() == null ? "user" : p.getRole(),
                        "content", p.getContent()
                ));
            }

            // ---- Request Body ----
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", chat.getModel());
            requestBody.put("messages", requestMessages);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // ---- API Call ----
            ResponseEntity<String> response = restTemplate.exchange(
                    API_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // ---- Save response ----
            newPayload.setResponse(response.getBody());
            chat.setMessages(payloads);

            messagesrepo.save(chat);

            ArrayList<messages> userChats = user.getMessages();
            if (userChats == null) userChats = new ArrayList<>();
            if (!userChats.contains(chat)) userChats.add(chat);
            user.setMessages(userChats);
            userRepository.save(user);

            return response.getBody();
        }
    }

    
    public String sendpdft(String id, MultipartFile multipartFile,String extratext,String model,String email) throws IOException {
        Optional<messages> chatOpt = messagesrepo.findById(id);
        user user = userRepository.findByemail(email);
        String textt = extractTextFromPdf(multipartFile);
        String prompt = textt + extratext;

        // ------------------- CASE 1: Create new chat -------------------
        if (!chatOpt.isPresent()) {
            // ---- Headers ----
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(API_KEY);
            
            // ---- Messages in API format ----
            List<Map<String, String>> requestMessages = new ArrayList<>();
            requestMessages.add(Map.of("role", "assistant", "content", ""));
            requestMessages.add(Map.of("role", "user", "content", prompt));

            // ---- Request Body ----
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("temperature", 1);
            requestBody.put("max_completion_tokens", 1024);
            requestBody.put("top_p", 1);
            requestBody.put("stream", false);
            requestBody.put("stop", null);
            requestBody.put("messages", requestMessages);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // ---- API Call ----
            ResponseEntity<String> response = restTemplate.exchange(
                    API_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // ---- Save in DB ----
            messages newChat = new messages();
            payload payload = new payload();
            payload.setRole("user");
            payload.setContent(prompt);
            payload.setResponse(response.getBody());

            ArrayList<payload> payloads = new ArrayList<>();
            payloads.add(payload);
            newChat.setMessages(payloads);

            messagesrepo.save(newChat);

            ArrayList<messages> userChats = user.getMessages();
            if (userChats == null) userChats = new ArrayList<>();
            userChats.add(newChat);
            user.setMessages(userChats);
            userRepository.save(user);

            return response.getBody();
        }

        // ------------------- CASE 2: Append to existing chat -------------------
        else {
            messages chat = chatOpt.get();

            // ---- Headers ----
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(API_KEY);

            // ---- Update chat ----
            chat.setModel(model);
            payload newPayload = new payload();
            newPayload.setRole("user");
            newPayload.setContent(prompt);

            ArrayList<payload> payloads = chat.getMessages();
            if (payloads == null) payloads = new ArrayList<>();
            payloads.add(newPayload);
            chat.setMessages(payloads);

            // ---- Build API Messages ----
            List<Map<String, String>> requestMessages = new ArrayList<>();
            for (payload p : payloads) {
                requestMessages.add(Map.of(
                        "role", p.getRole() == null ? "user" : p.getRole(),
                        "content", p.getContent()
                ));
            }

            // ---- Request Body ----
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", chat.getModel());
            requestBody.put("messages", requestMessages);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // ---- API Call ----
            ResponseEntity<String> response = restTemplate.exchange(
                    API_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // ---- Save response ----
            newPayload.setResponse(response.getBody());
            chat.setMessages(payloads);

            messagesrepo.save(chat);

            ArrayList<messages> userChats = user.getMessages();
            if (userChats == null) userChats = new ArrayList<>();
            if (!userChats.contains(chat)) userChats.add(chat);
            user.setMessages(userChats);
            userRepository.save(user);

            return response.getBody();
        }
    }
    private String extractTextFromPdf(MultipartFile pdfFile) throws IOException {
    try (InputStream inputStream = pdfFile.getInputStream();
         PDDocument document = PDDocument.load(inputStream)) {

        PDFTextStripper pdfStripper = new PDFTextStripper();
        return pdfStripper.getText(document);
    }
     }

    
    public String senddocx(String id, MultipartFile multipartFile,String extratext,String model,String email) throws IOException {
        Optional<messages> chatOpt = messagesrepo.findById(id);
        user user = userRepository.findByemail(email);
        String textt = extractTextFromDocx(multipartFile);
        String prompt = textt + extratext;

        // ------------------- CASE 1: Create new chat -------------------
        if (!chatOpt.isPresent()) {
            // ---- Headers ----
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(API_KEY);
            
            // ---- Messages in API format ----
            List<Map<String, String>> requestMessages = new ArrayList<>();
            requestMessages.add(Map.of("role", "assistant", "content", ""));
            requestMessages.add(Map.of("role", "user", "content", prompt));

            // ---- Request Body ----
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("temperature", 1);
            requestBody.put("max_completion_tokens", 1024);
            requestBody.put("top_p", 1);
            requestBody.put("stream", false);
            requestBody.put("stop", null);
            requestBody.put("messages", requestMessages);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // ---- API Call ----
            ResponseEntity<String> response = restTemplate.exchange(
                    API_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // ---- Save in DB ----
            messages newChat = new messages();
            payload payload = new payload();
            payload.setRole("user");
            payload.setContent(prompt);
            payload.setResponse(response.getBody());

            ArrayList<payload> payloads = new ArrayList<>();
            payloads.add(payload);
            newChat.setMessages(payloads);

            messagesrepo.save(newChat);

            ArrayList<messages> userChats = user.getMessages();
            if (userChats == null) userChats = new ArrayList<>();
            userChats.add(newChat);
            user.setMessages(userChats);
            userRepository.save(user);

            return response.getBody();
        }

        // ------------------- CASE 2: Append to existing chat -------------------
        else {
            messages chat = chatOpt.get();

            // ---- Headers ----
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(API_KEY);

            // ---- Update chat ----
            chat.setModel(model);
            payload newPayload = new payload();
            newPayload.setRole("user");
            newPayload.setContent(prompt);

            ArrayList<payload> payloads = chat.getMessages();
            if (payloads == null) payloads = new ArrayList<>();
            payloads.add(newPayload);
            chat.setMessages(payloads);

            // ---- Build API Messages ----
            List<Map<String, String>> requestMessages = new ArrayList<>();
            for (payload p : payloads) {
                requestMessages.add(Map.of(
                        "role", p.getRole() == null ? "user" : p.getRole(),
                        "content", p.getContent()
                ));
            }

            // ---- Request Body ----
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", chat.getModel());
            requestBody.put("messages", requestMessages);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // ---- API Call ----
            ResponseEntity<String> response = restTemplate.exchange(
                    API_URL,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // ---- Save response ----
            newPayload.setResponse(response.getBody());
            chat.setMessages(payloads);

            messagesrepo.save(chat);

            ArrayList<messages> userChats = user.getMessages();
            if (userChats == null) userChats = new ArrayList<>();
            if (!userChats.contains(chat)) userChats.add(chat);
            user.setMessages(userChats);
            userRepository.save(user);

            return response.getBody();
        }
    }

     





private String extractTextFromDocx(MultipartFile docxFile) throws IOException {
    StringBuilder text = new StringBuilder();
    try (InputStream inputStream = docxFile.getInputStream();
         XWPFDocument document = new XWPFDocument(inputStream)) {

        for (XWPFParagraph paragraph : document.getParagraphs()) {
            text.append(paragraph.getText()).append("\n");
        }
    }
    return text.toString();
}

public Optional<messages> getmsgg(String id){
    Optional<messages> messages = messagesrepo.findById(id);
    return messages;
}
}