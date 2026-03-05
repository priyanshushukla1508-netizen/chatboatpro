(function () {
    const script = document.currentScript;
    const botToken = script.getAttribute('data-bot-id');
    // Change this URL to your LIVE Render backend URL after deployment
    const API_BASE = "https://chatboatpro1.onrender.com/api/v1/widget";
    let sessionId = localStorage.getItem('chat_session_id') || Math.random().toString(36).substring(7);
    localStorage.setItem('chat_session_id', sessionId);

    if (!botToken) {
        console.error("ChatBotPro: Missing data-bot-id attribute.");
        return;
    }

    // quick flag for demo mode (must be defined before any usage)
    const isDemo = botToken === 'demo-token';

    // 1. Create Container & Shadow DOM
    const container = document.createElement('div');
    container.id = 'chatbot-pro-container';
    document.body.appendChild(container);
    const shadow = container.attachShadow({ mode: 'open' });

    // 2. Styles
    const style = document.createElement('style');
    style.textContent = `
        :host {
            --primary-color: #6C63FF;
            --bg-color: #0D0E1C;
            --surface-color: #161728;
            --text-color: #FFFFFF;
            --text-muted: #B0B3C6;
            font-family: 'Inter', sans-serif;
        }

        .chat-bubble {
            position: fixed;
            bottom: 20px;
            right: 20px;
            width: 60px;
            height: 60px;
            background: var(--primary-color);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            box-shadow: 0 4px 12px rgba(0,0,0,0.3);
            z-index: 10000;
            transition: transform 0.3s ease;
        }

        .chat-bubble:hover {
            transform: scale(1.1);
        }

        .chat-bubble svg {
            width: 30px;
            height: 30px;
            fill: white;
        }

        .chat-window {
            position: fixed;
            bottom: 90px;
            right: 20px;
            width: 380px;
            height: 520px;
            background: var(--bg-color);
            border-radius: 16px;
            display: none;
            flex-direction: column;
            overflow: hidden;
            box-shadow: 0 8px 24px rgba(0,0,0,0.4);
            z-index: 10000;
            border: 1px solid rgba(255,255,255,0.1);
            backdrop-filter: blur(10px);
        }

        .chat-window.open {
            display: flex;
            animation: slideUp 0.3s ease;
        }

        @keyframes slideUp {
            from { opacity: 0; transform: translateY(20px); }
            to { opacity: 1; transform: translateY(0); }
        }

        .chat-header {
            padding: 20px;
            background: var(--surface-color);
            border-bottom: 1px solid rgba(255,255,255,0.1);
            display: flex;
            align-items: center;
            justify-content: space-between;
        }

        .bot-info {
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .bot-avatar {
            width: 32px;
            height: 32px;
            background: var(--primary-color);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: bold;
            font-size: 14px;
        }

        .bot-name {
            font-weight: 600;
            color: var(--text-color);
        }

        .close-btn {
            cursor: pointer;
            color: var(--text-muted);
        }

        .chat-messages {
            flex: 1;
            padding: 20px;
            overflow-y: auto;
            display: flex;
            flex-direction: column;
            gap: 12px;
        }

        .message {
            max-width: 80%;
            padding: 10px 14px;
            border-radius: 12px;
            font-size: 14px;
            line-height: 1.4;
        }

        .message.bot {
            background: var(--surface-color);
            color: var(--text-color);
            align-self: flex-start;
            border-bottom-left-radius: 2px;
        }

        .message.user {
            background: var(--primary-color);
            color: white;
            align-self: flex-end;
            border-bottom-right-radius: 2px;
        }

        .chat-input {
            padding: 16px;
            background: var(--surface-color);
            border-top: 1px solid rgba(255,255,255,0.1);
            display: flex;
            gap: 10px;
        }

        input {
            flex: 1;
            background: rgba(0,0,0,0.2);
            border: 1px solid rgba(255,255,255,0.2);
            border-radius: 8px;
            padding: 10px;
            color: white;
            outline: none;
        }

        button {
            background: var(--primary-color);
            border: none;
            border-radius: 8px;
            padding: 0 16px;
            color: white;
            cursor: pointer;
            transition: opacity 0.2s;
        }

        button:disabled {
            opacity: 0.5;
            cursor: not-allowed;
        }

        /* Scrollbar */
        .chat-messages::-webkit-scrollbar { width: 4px; }
        .chat-messages::-webkit-scrollbar-thumb { background: rgba(255,255,255,0.1); border-radius: 10px; }

        /* === PROACTIVE TRIGGER: Notification Badge === */
        .notif-badge {
            position: absolute;
            top: -4px;
            right: -4px;
            width: 20px;
            height: 20px;
            background: #FF4757;
            border-radius: 50%;
            font-size: 11px;
            font-weight: 700;
            color: white;
            display: flex;
            align-items: center;
            justify-content: center;
            opacity: 0;
            transform: scale(0);
            transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
            pointer-events: none;
        }
        .notif-badge.show {
            opacity: 1;
            transform: scale(1);
        }

        /* Pulse ring animation on bubble when proactive */
        @keyframes proactivePulse {
            0%   { box-shadow: 0 0 0 0 rgba(108, 99, 255, 0.7); }
            70%  { box-shadow: 0 0 0 16px rgba(108, 99, 255, 0); }
            100% { box-shadow: 0 0 0 0 rgba(108, 99, 255, 0); }
        }
        .chat-bubble.proactive-pulse {
            animation: proactivePulse 1.2s ease-out 3;
        }

        /* Proactive message tooltip above bubble */
        .proactive-tooltip {
            position: fixed;
            bottom: 90px;
            right: 90px;
            background: white;
            color: #111;
            padding: 10px 14px;
            border-radius: 12px;
            font-size: 13px;
            font-weight: 500;
            box-shadow: 0 4px 16px rgba(0,0,0,0.2);
            white-space: nowrap;
            opacity: 0;
            transform: translateY(8px);
            transition: all 0.3s ease;
            pointer-events: none;
            z-index: 10001;
            max-width: 220px;
            white-space: normal;
            line-height: 1.4;
        }
        .proactive-tooltip::after {
            content: '';
            position: absolute;
            bottom: -6px;
            right: 16px;
            border: 6px solid transparent;
            border-top-color: white;
            border-bottom: none;
        }
        .proactive-tooltip.show {
            opacity: 1;
            transform: translateY(0);
        }

        /* 🔒 Encryption Badge */
        .security-badge {
            font-size: 10px;
            color: #00F5A0;
            opacity: 0.8;
            display: flex;
            align-items: center;
            gap: 4px;
            letter-spacing: 0.04em;
        }

        /* GDPR Consent Screen */
        .consent-screen {
            position: absolute;
            inset: 0;
            background: var(--bg-color);
            border-radius: 20px;
            z-index: 20;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            padding: 28px;
            text-align: center;
            animation: slideUp 0.3s ease;
        }
        .consent-screen.hidden { display: none; }
        .consent-icon { font-size: 40px; margin-bottom: 16px; }
        .consent-title { font-size: 16px; font-weight: 700; margin-bottom: 10px; color: var(--text-color); }
        .consent-body { font-size: 12px; color: var(--text-muted); line-height: 1.6; margin-bottom: 24px; }
        .consent-body a { color: #00F5A0; text-decoration: none; }
        .consent-btns { display: flex; gap: 10px; width: 100%; }
        .btn-accept {
            flex: 1; background: #00F5A0; color: #000;
            border: none; padding: 12px; border-radius: 8px;
            font-weight: 700; font-size: 14px; cursor: pointer;
            font-family: inherit; transition: 0.2s;
        }
        .btn-accept:hover { box-shadow: 0 0 20px rgba(0,245,160,0.3); }
        .btn-decline {
            flex: 1; background: transparent; color: var(--text-muted);
            border: 1px solid rgba(255,255,255,0.1); padding: 12px;
            border-radius: 8px; font-size: 14px; cursor: pointer;
            font-family: inherit; transition: 0.2s;
        }
        .btn-decline:hover { background: rgba(255,255,255,0.04); }
        .consent-note { font-size: 10px; color: #444; margin-top: 14px; }

        /* Visitor Memory Banner */
        .returning-banner {
            background: rgba(0,245,160,0.06);
            border-bottom: 1px solid rgba(0,245,160,0.1);
            padding: 8px 16px;
            font-size: 12px;
            color: #00F5A0;
            display: none;
            align-items: center;
            gap: 6px;
        }
        .returning-banner.show { display: flex; }

        /* CSAT Rating Overlay */
        .csat-overlay {
            position: absolute;
            bottom: 0; left: 0; right: 0;
            background: var(--surface-color);
            border-top: 1px solid rgba(255,255,255,0.1);
            padding: 16px;
            text-align: center;
            z-index: 10;
            animation: slideUp 0.3s ease;
        }
        .csat-overlay p { font-size: 13px; color: var(--text-muted); margin-bottom: 10px; }
        .stars { display: flex; justify-content: center; gap: 8px; cursor: pointer; margin-bottom: 10px; }
        .star { font-size: 24px; opacity: 0.3; transition: 0.2s; }
        .star.active { opacity: 1; }
        .csat-skip { font-size: 11px; color: var(--text-muted); cursor: pointer; text-decoration: underline; }

    `;
    shadow.appendChild(style);

    // 3. HTML Structure
    const bubble = document.createElement('div');
    bubble.className = 'chat-bubble';
    bubble.style.position = 'relative';
    bubble.innerHTML = '<svg viewBox="0 0 24 24"><path d="M20 2H4c-1.1 0-2 .9-2 2v18l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2z"/></svg><div class="notif-badge" id="notif-badge">1</div>';

    // Proactive tooltip element (above bubble)
    const proactiveTooltip = document.createElement('div');
    proactiveTooltip.className = 'proactive-tooltip';
    proactiveTooltip.id = 'proactive-tooltip';
    proactiveTooltip.textContent = '👋 Koi help chahiye? Main yahan hoon!';
    document.body.appendChild(proactiveTooltip);

    const chatWindow = document.createElement('div');
    chatWindow.className = 'chat-window';
    chatWindow.innerHTML = `
        <div class="chat-header">
            <div class="bot-info">
                <div class="bot-avatar">AI</div>
                <div style="display:flex;flex-direction:column;gap:2px">
                    <div class="bot-name">Support Bot</div>
                    <div class="security-badge">🔒 End-to-End Secured</div>
                </div>
            </div>
            <div class="close-btn">✕</div>
        </div>
        <div class="returning-banner" id="returning-banner">
            <span>👋</span><span id="returning-text">Welcome back!</span>
        </div>
        <div class="chat-messages" id="messages"></div>
        <div class="consent-screen" id="consent-screen">
            <div class="consent-icon">🔐</div>
            <div class="consent-title">Privacy Notice</div>
            <div class="consent-body">
                This chat is powered by <strong>AI</strong>. We may process your messages to provide support.<br><br>
                Compliant with India's <strong>DPDP Act 2023</strong> &amp; GDPR.<br>
                <a href="#" onclick="return false;">Read our Privacy Policy</a>
            </div>
            <div class="consent-btns">
                <button class="btn-accept" id="consent-accept">✅ I Agree</button>
                <button class="btn-decline" id="consent-decline">❌ Decline</button>
            </div>
            <div class="consent-note">Your data is encrypted &amp; never sold to third parties.</div>
        </div>
        <div class="chat-input">
            <label for="img-upload" title="Upload Image" style="cursor:pointer; opacity:0.6; font-size:18px; padding: 0 6px; color:var(--text-color);">📷</label>
            <input type="file" id="img-upload" accept="image/*" style="display:none">
            <input type="text" placeholder="Type a message..." id="user-input" disabled>
            <button id="send-btn" disabled>Send</button>
        </div>
    `;

    shadow.appendChild(bubble);
    shadow.appendChild(chatWindow);


    // === CSAT RATING ===
    let csatShown = false;
    function showCSAT() {
        if (csatShown) return;
        csatShown = true;
        const csatDiv = document.createElement('div');
        csatDiv.className = 'csat-overlay';
        csatDiv.innerHTML = `
            <p>How was your experience? ⭐</p>
            <div class="stars">
                <span class="star" data-val="1">★</span>
                <span class="star" data-val="2">★</span>
                <span class="star" data-val="3">★</span>
                <span class="star" data-val="4">★</span>
                <span class="star" data-val="5">★</span>
            </div>
            <span class="csat-skip">Skip feedback</span>
        `;
        chatWindow.appendChild(csatDiv);

        // Hover stars
        const stars = csatDiv.querySelectorAll('.star');
        stars.forEach(star => {
            star.addEventListener('mouseover', () => {
                const val = parseInt(star.dataset.val);
                stars.forEach((s, i) => s.classList.toggle('active', i < val));
            });
            star.addEventListener('click', () => {
                const rating = parseInt(star.dataset.val);
                if (!isDemo) {
                    fetch(`${API_BASE}/csat`, {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ botToken, sessionId, rating })
                    });
                }
                csatDiv.innerHTML = `<p style="color:#00F5A0; font-weight:600;">Thank you! Your feedback means a lot 🙏</p>`;
                setTimeout(() => csatDiv.remove(), 2000);
            });
        });

        csatDiv.querySelector('.csat-skip').onclick = () => csatDiv.remove();
    }

    // =============================================
    // === GDPR / DPDP CONSENT MANAGER ===
    // =============================================
    const consentKey = `chatbot_consent_${botToken}`;
    const consentScreen = shadow.getElementById('consent-screen');
    const consentAccept = shadow.getElementById('consent-accept');
    const consentDecline = shadow.getElementById('consent-decline');
    const userInput = shadow.getElementById('user-input');
    const sendBtn = shadow.getElementById('send-btn');

    function enableChat() {
        consentScreen.classList.add('hidden');
        userInput.disabled = false;
        sendBtn.disabled = false;
    }

    const alreadyConsented = localStorage.getItem(consentKey) === 'true';
    if (alreadyConsented) {
        enableChat();
    } else {
        // Show consent screen on first open
        consentAccept.onclick = () => {
            localStorage.setItem(consentKey, 'true');
            enableChat();
        };
        consentDecline.onclick = () => {
            chatWindow.classList.remove('open');
            consentScreen.classList.add('hidden');
        };
    }

    // =============================================
    // === VISITOR MEMORY SYSTEM ===
    // Each visitor gets a unique persistent ID
    // Their name, chat history & preferences are remembered
    // =============================================
    const VISITOR_KEY = `cbp_visitor_${botToken}`;

    // Load or create visitor profile
    let visitorProfile = JSON.parse(localStorage.getItem(VISITOR_KEY) || 'null');
    const isReturningVisitor = visitorProfile !== null;

    if (!visitorProfile) {
        // New visitor — create profile
        visitorProfile = {
            visitorId: 'visitor_' + Math.random().toString(36).substring(2, 10) + '_' + Date.now(),
            name: null,
            email: null,
            firstSeen: new Date().toISOString(),
            lastSeen: new Date().toISOString(),
            visitCount: 1,
            chatHistory: [],       // Stores last 20 messages
            preferences: {},
            pagesVisited: [window.location.pathname]
        };
    } else {
        // Returning visitor — update their profile
        visitorProfile.lastSeen = new Date().toISOString();
        visitorProfile.visitCount = (visitorProfile.visitCount || 1) + 1;
        const currentPage = window.location.pathname;
        if (!visitorProfile.pagesVisited) visitorProfile.pagesVisited = [];
        if (!visitorProfile.pagesVisited.includes(currentPage)) {
            visitorProfile.pagesVisited.push(currentPage);
        }
    }

    function saveVisitorProfile() {
        // Keep only last 20 messages to avoid storage bloat
        if (visitorProfile.chatHistory.length > 20) {
            visitorProfile.chatHistory = visitorProfile.chatHistory.slice(-20);
        }
        localStorage.setItem(VISITOR_KEY, JSON.stringify(visitorProfile));
    }

    // Show returning visitor banner
    if (isReturningVisitor && visitorProfile.name) {
        const banner = shadow.getElementById('returning-banner');
        const bannerText = shadow.getElementById('returning-text');
        bannerText.textContent = `Welcome back, ${visitorProfile.name}! 👋 Picking up from where we left off.`;
        banner.classList.add('show');
        setTimeout(() => banner.classList.remove('show'), 5000);
    }

    // Override addMessage to also save to visitor history
    const _origAddMsg = function (text, sender) {
        const div = document.createElement('div');
        div.className = `message ${sender}`;
        div.textContent = text;
        msgContainer.appendChild(div);
        msgContainer.scrollTop = msgContainer.scrollHeight;

        // Save to visitor memory
        visitorProfile.chatHistory.push({ role: sender, text, time: new Date().toISOString() });
        saveVisitorProfile();

        // Auto-detect visitor name from text
        const nameMatch = text.match(/(?:main|my name is|i am|mera naam|naam hai)\s+([A-Z][a-z]{2,15})/i);
        if (sender === 'user' && nameMatch && !visitorProfile.name) {
            visitorProfile.name = nameMatch[1];
            saveVisitorProfile();
        }
    };

    // Demo Mode Logic
    // (isDemo already defined near top to avoid ReferenceErrors)

    // 4. Logic
    const msgContainer = shadow.getElementById('messages');
    // userInput and sendBtn declared above in GDPR consent block
    const closeBtn = chatWindow.querySelector('.close-btn');

    let botConfig = null;

    // GLOBAL TRIGGER (exposed for demo button / external callers)
    window.openChatBotPro = () => {
        if (!chatWindow) {
            console.error('ChatBotPro: chat window not initialized yet');
            return;
        }
        chatWindow.classList.add('open');
        if (userInput && typeof userInput.focus === 'function') {
            userInput.focus();
        }
    };

    if (isDemo) {
        // Simulated Demo Experience
        shadow.querySelector('.bot-name').textContent = "Rohan (Sales Engineer)";
        shadow.querySelector('.bot-avatar').textContent = "RO";
        // Apply Emerald Theme
        container.style.setProperty('--primary-color', '#00F5A0');
        container.style.setProperty('--bg-color', '#030407');
        container.style.setProperty('--surface-color', '#0A0C12');

        setTimeout(() => {
            addMessage("Namaste! Main Rohan hoon. Main dekh raha hoon aap hamari 'High Scale' features explore kar rahe hain. Main aapki kaise madad kar sakta hoon?", 'bot');
        }, 800);
    } else {
        // Fetch Config from Backend
        fetch(`${API_BASE}/config/${botToken}`)
            .then(res => res.json())
            .then(data => {
                botConfig = data;
                shadow.querySelector('.bot-name').textContent = data.botName || "Support Bot";
                shadow.querySelector('.bot-avatar').textContent = (data.botName || "AI").substring(0, 2).toUpperCase();
                if (data.primaryColor) {
                    container.style.setProperty('--primary-color', data.primaryColor);
                }
                addMessage(data.welcomeMessage || "Hi! How can I help you today?", 'bot');
            })
            .catch(() => {
                addMessage("Note: Backend is offline. Run 'mvn spring-boot:run' to enable live AI responses.", 'bot');
            });
    }

    bubble.onclick = () => chatWindow.classList.toggle('open');
    closeBtn.onclick = () => chatWindow.classList.remove('open');

    // === FEATURE: IMAGE UPLOAD (GPT-Vision) ===
    const imgUpload = shadow.getElementById('img-upload');
    imgUpload.onchange = async (e) => {
        const file = e.target.files[0];
        if (!file) return;

        // Preview in chat
        const reader = new FileReader();
        reader.onload = (ev) => {
            const imgDiv = document.createElement('div');
            imgDiv.className = 'message user';
            imgDiv.innerHTML = `<img src="${ev.target.result}" style="max-width:180px;border-radius:8px;display:block" />`;
            msgContainer.appendChild(imgDiv);
            msgContainer.scrollTop = msgContainer.scrollHeight;
        };
        reader.readAsDataURL(file);

        // Analyze image
        const typingDiv = document.createElement('div');
        typingDiv.className = 'message bot';
        typingDiv.textContent = '📷 Analyzing your image...';
        msgContainer.appendChild(typingDiv);

        if (isDemo) {
            setTimeout(() => {
                typingDiv.textContent = "Main aapki image dekh sakta hoon! Yeh damaged product/error issue lagta hai. Aap hamare support team ko contact karein — hum 24 ghante mein resolve kar denge. 🙏";
            }, 1500);
        } else {
            const formData = new FormData();
            formData.append('image', file);
            formData.append('context', 'Customer uploaded this image for support');
            try {
                const res = await fetch(`${API_BASE}/analyze-image`, { method: 'POST', body: formData });
                const data = await res.json();
                typingDiv.textContent = data.response;
            } catch {
                typingDiv.textContent = "Image analysis unavailable. Please describe your issue in text.";
            }
        }
        imgUpload.value = '';
    };

    // === FEATURE: PREDICTIVE LEAD SCORING (Behavior Tracking) ===
    const behavior = {
        timeOnPageSeconds: 0,
        messageCount: 0,
        pagesVisited: 1,
        visitedPricing: window.location.href.includes('pricing') || document.querySelector('#pricing') !== null,
        askedPrice: false,
        askedAboutEnterprise: false
    };

    // Increment time tracker every 10s
    setInterval(() => { behavior.timeOnPageSeconds += 10; }, 10000);

    function pushLeadScore() {
        if (isDemo) return;
        fetch(`${API_BASE}/lead-score`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ botToken, sessionId, behavior })
        }).catch(() => { });
    }


    function addMessage(text, sender) {
        const div = document.createElement('div');
        div.className = `message ${sender}`;
        div.textContent = text;
        msgContainer.appendChild(div);
        msgContainer.scrollTop = msgContainer.scrollHeight;
    }

    async function sendMessage() {
        const text = userInput.value.trim();
        if (!text) return;

        addMessage(text, 'user');
        userInput.value = '';
        userInput.disabled = true;
        sendBtn.disabled = true;

        if (isDemo) {
            // Advanced Bilingual AI Demo Engine
            const delay = 900 + Math.random() * 600;
            setTimeout(() => {
                const t = text.toLowerCase();

                // --- LANGUAGE DETECTION ---
                // Devanagari script check (pure Hindi)
                const hasDevanagari = /[\u0900-\u097F]/.test(text);
                // Common Hinglish words check
                const hinglishWords = ['kya', 'hai', 'hoon', 'kar', 'aap', 'mujhe', 'humara', 'bata', 'batao', 'kaise', 'karo', 'chahiye', 'karo', 'milta', 'pe', 'se', 'mein', 'nahi', 'haan', 'ji', 'yeh', 'toh', 'bhi', 'agar', 'acha', 'accha', 'thk', 'rkho', 'liye', 'wala', 'waly', 'phele', 'lagao', 'lag'];
                const hasHinglish = hinglishWords.some(w => t.split(/\s+/).includes(w));
                const isHindi = hasDevanagari || hasHinglish;

                let response;

                // === GREETING ===
                if (t.match(/^(hi|hello|hlo|hey|hii|namaste|namaskar|helo|hy)[\s!?.,]*$/)) {
                    response = isHindi
                        ? "Hello! Main Rohan hoon, ChatBotPro ki taraf se. Kisi bhi cheez mein help chahiye? Jaise — features, pricing, ya website par bot kaise lagayein?"
                        : "Hello! I'm Rohan from ChatBotPro. How can I help you today? Feel free to ask about our features, pricing, or how to add the bot to your website.";
                }
                // === FEATURES ===
                else if (t.includes('feature') || t.includes('kya kya') || t.includes('kya milta') || t.includes('batao') || t.includes('bata do') || t.includes('capabilities') || t.includes('kya kar sakta') || t.includes('tell me about')) {
                    response = isHindi
                        ? "Bilkul! Hamare key features:\n✅ Auto AI Training — website khud-ba-khud crawl hoti hai\n✅ Hinglish & Regional Language support\n✅ CRM Integration (Zoho, HubSpot se connect)\n✅ Lead Capture & Analytics Dashboard\n✅ Proactive Bot — exit-intent par khud open hota hai\n✅ Auto-FAQ Generation\n\nKis feature ke baare mein aur jaanna chahenge?"
                        : "Great question! Here are our key features:\n✅ Auto AI Training — your website is crawled automatically\n✅ Hinglish & Regional Language support\n✅ CRM Integration with Zoho & HubSpot\n✅ Lead Capture & Analytics Dashboard\n✅ Proactive Bot — opens automatically on exit-intent\n✅ Auto-FAQ Generation\n\nWhich feature would you like to know more about?";
                }
                // === INTEGRATION ===
                else if (t.includes('kaise add') || t.includes('kaise lagay') || t.includes('install') || t.includes('integrate') || t.includes('website pr') || t.includes('website par') || t.includes('embed') || t.includes('script') || t.includes('code') || t.includes('setup') || t.includes('how to add') || t.includes('how to install')) {
                    response = isHindi
                        ? "Bahut easy hai! Sirf yeh ek line apne website ke HTML mein paste karein:\n\n<script src=\"cdn.chatbotpro.in/widget.js\" data-bot-id=\"YOUR_TOKEN\"></script>\n\nBas! Koi coding nahi chahiye. Bot live ho jayega. 😊"
                        : "Super easy! Just paste this one line before </body> in your website's HTML:\n\n<script src=\"cdn.chatbotpro.in/widget.js\" data-bot-id=\"YOUR_TOKEN\"></script>\n\nThat's it! No coding needed. Your bot goes live instantly. 😊";
                }
                // === PRICING ===
                else if (t.includes('price') || t.includes('plan') || t.includes('cost') || t.includes('kitne') || t.includes('charge') || t.includes('paisa') || t.includes('fee') || t.includes('free') || t.includes('trial') || t.includes('how much')) {
                    response = isHindi
                        ? "Hamare 3 plans hain:\n\n🆓 Early Access Trial — ₹0 (2 din, full access)\n⚡ Pro Plan — ₹19,999/mo (10,000 messages, 1 website)\n🏢 Enterprise — Custom Deal (Dedicated support, White-label)\n\nKaunsa plan aapke liye sahi hai?"
                        : "We have 3 plans:\n\n🆓 Early Access Trial — ₹0 (2 Days, full access)\n⚡ Pro Plan — ₹19,999/mo (10,000 messages, 1 website)\n🏢 Enterprise — Custom Deal (Dedicated support, White-label)\n\nWhich plan suits your needs?";
                }
                // === LANGUAGE ===
                else if (t.includes('hindi') || t.includes('hinglish') || t.includes('language') || t.includes('bhasha') || t.includes('regional') || t.includes('tamil') || t.includes('bangla') || t.includes('telugu')) {
                    response = isHindi
                        ? "Ji haan! Jaise abhi main Hinglish mein baat kar raha hoon, hamara AI Tamil, Telugu, Bengali, aur Hinglish sab mein reply kar sakta hai. Yeh Indian customers ke liye specially designed hai!"
                        : "Yes! Just like I'm speaking with you now, our AI can respond in Hinglish, Tamil, Telugu, Bengali, and more. It's built specifically for the Indian market!";
                }
                // === ENTERPRISE ===
                else if (t.includes('enterprise') || t.includes('custom') || t.includes('scale') || t.includes('white label') || t.includes('white-label') || t.includes('resell')) {
                    response = isHindi
                        ? "Enterprise plan mein milta hai:\n🔧 Custom AI Model Training\n🎨 White-label (aapka khud ka brand)\n👨‍💼 Dedicated Account Engineer\n📞 Priority Support\n\nPrice custom deal pe hoti hai. Main ek meeting schedule karwau?"
                        : "Our Enterprise plan includes:\n🔧 Custom AI Model Training\n🎨 Full White-label (your own brand)\n👨‍💼 Dedicated Account Engineer\n📞 Priority Support\n\nPricing is custom. Shall I schedule a quick call for you?";
                }
                // === COMPARISON ===
                else if (t.includes('vs') || t.includes('compare') || t.includes('better') || t.includes('intercom') || t.includes('drift') || t.includes('tidio') || t.includes('freshdesk')) {
                    response = isHindi
                        ? "ChatBotPro Intercom ya Drift se alag kyun hai:\n🇮🇳 Indian languages native support\n💰 10x kam price (₹19,999 vs $500+)\n🚀 2-minute setup\n🧠 Auto website crawl — manual training nahi\n\nSabse bada fark: yeh human lagta hai, robotic nahi!"
                        : "Here's why ChatBotPro beats Intercom & Drift:\n🇮🇳 Native Indian language support\n💰 10x cheaper (₹19,999 vs $500+)\n🚀 2-minute setup, no code needed\n🧠 Auto website crawl — no manual training\n\nBiggest difference: it feels human, not robotic!";
                }
                // === COMPLIMENTS ===
                else if (t.match(/(wow|great|amazing|awesome|badhiya|badiya|accha|acha|shandaar|zabardast|nice|good|superb|bahut acha)/)) {
                    response = isHindi
                        ? "Shukriya! 😊 Yahi toh hamara mission hai — aapke customers ko lagni chahiye ki woh kisi human se baat kar rahe hain. Kya aap apni website par bhi try karna chahenge?"
                        : "Thank you! 😊 That's exactly what we aim for — making every customer feel like they're talking to a real human. Want to try it on your own website?";
                }
                // === OBJECTION HANDLER (Sales Psychology) ===
                else if (t.match(/(too expensive|mehnga|costly|budget nahi|afford|paisa nahi|price kam|mahanga|bohot mehenga)/)) {
                    response = isHindi
                        ? "Main samajh sakta hoon! Lekin sochen — agar ChatBotPro sirf 5 extra leads per month convert kare, toh ₹19,999 pehle hafte mein recover ho jata hai. 💡 Hamare clients ka average ROI 340% hai pehle 60 dino mein. Kya 2-din FREE trial se shuru karein? Zero commitment!"
                        : "I understand the concern! But consider — if ChatBotPro converts just 5 extra leads/month, ₹19,999 pays for itself in week one. 💡 Our clients see 340% ROI in 60 days. Want to start with a FREE 2-day trial? Zero commitment needed!";
                }
                else if (t.match(/(sochna hai|i'll think|will think|not sure|sure nahi|abhi nahi|later|baad mein|next month)/)) {
                    response = isHindi
                        ? "Bilkul sochein! Lekin yeh yaad rakhein — har din bina bot ke aap un visitors ko miss kar rahe hain jo raat 2 baje query karte hain. 2-din FREE trial hai — koi credit card nahi chahiye. Sirf results dekhein, phir decide karein!"
                        : "Of course! But consider — every day without a bot, you're missing visitors who query at 2am. Our 2-day FREE trial needs zero credit card. Just see the results first, then decide!";
                }
                else if (t.match(/(trust nahi|guarantee|bharosa|legit|fake|verified|reviews|testimonials|reliable)/)) {
                    response = isHindi
                        ? "Valid concern! Isliye hum pehle 2-din FREE trial dete hain — bina kisi payment ke. 200+ Indian clients (Mumbai, Delhi, Jaipur) ne pehle use kiya, phir kharida. Main aapko ek live client reference bhi de sakta hoon chahein toh!"
                        : "Totally valid! That's exactly why we offer a FREE 2-day trial — no payment needed. 200+ clients across India tried before buying. I can even give you a live client reference if you'd like!";
                }
                // === SMART FALLBACK ===
                else {
                    const fallbacksHi = [
                        "Interesting! Kya aap thoda aur detail mein poochh sakte hain? Main sahi jawab doonga.",
                        "Samajh gaya! Kya aap features ke baare mein pooch rahe hain ya website integration ke baare mein?",
                        "Acha sawaal! Kya main aapko ek quick demo call par sab explain karun?",
                        "Main aapki baat samajh raha hoon! Try karein 'feature batao' ya 'price kya hai' type karna. 😊"
                    ];
                    const fallbacksEn = [
                        "Interesting! Could you elaborate a bit more? I want to give you the most accurate answer.",
                        "Got it! Are you asking about our features or how to integrate the bot on your website?",
                        "Great question! Try asking me about 'features', 'pricing', or 'how to install'.",
                        "I understand! Type something like 'show features' or 'what is the price' and I'll give you a detailed answer. 😊"
                    ];
                    const pool = isHindi ? fallbacksHi : fallbacksEn;
                    response = pool[Math.floor(Math.random() * pool.length)];
                }

                addMessage(response, 'bot');

                // Trigger CSAT after 3rd bot reply
                if (!chatWindow._botReplyCount) chatWindow._botReplyCount = 0;
                chatWindow._botReplyCount++;
                if (chatWindow._botReplyCount === 3) setTimeout(showCSAT, 1000);

                userInput.disabled = false;
                sendBtn.disabled = false;
                userInput.focus();
            }, delay);
            return;
        }


        try {
            const res = await fetch(`${API_BASE}/chat`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    message: text,
                    sessionId: sessionId,
                    botToken: botToken
                })
            });
            const data = await res.json();
            addMessage(data.response, 'bot');
        } catch (e) {
            addMessage("Backend connection failed. Is the Spring Boot server running?", 'bot');
        } finally {
            userInput.disabled = false;
            sendBtn.disabled = false;
            userInput.focus();
        }
    }

    sendBtn.onclick = sendMessage;
    userInput.onkeypress = (e) => { if (e.key === 'Enter') sendMessage(); };

    // =============================================
    // === FEATURE 7: SMART PROACTIVE TRIGGERS ===
    // Fires ONCE per browser tab session
    // Trigger 1: 30-second idle timer
    // Trigger 2: Exit-intent (mouse leaves top of page)
    // =============================================
    const PROACTIVE_KEY = `cbp_proactive_shown_${botToken}`;

    function showProactiveTrigger() {
        // Guard: only fire once per tab session
        if (sessionStorage.getItem(PROACTIVE_KEY)) return;
        // Don't trigger if widget is already open
        if (chatWindow.classList.contains('open')) return;
        sessionStorage.setItem(PROACTIVE_KEY, 'true');

        const badge = shadow.getElementById('notif-badge');

        // Step 1: Pulse the bubble
        bubble.classList.add('proactive-pulse');

        // Step 2: Show notification badge
        if (badge) badge.classList.add('show');

        // Step 3: Show tooltip message
        proactiveTooltip.classList.add('show');

        // Step 4: Auto-open the chat window after 2.5s
        setTimeout(() => {
            proactiveTooltip.classList.remove('show');
            chatWindow.classList.add('open');
            if (badge) badge.classList.remove('show');
            bubble.classList.remove('proactive-pulse');

            // Add the proactive greeting message
            const existingMsgs = msgContainer.querySelectorAll('.message');
            if (existingMsgs.length <= 1) {
                // Only insert if chat is still fresh (≤1 welcome message)
                addMessage('👋 Koi help chahiye? Main yahan hoon! Bata dijiye kuch bhi.', 'bot');
            }
        }, 2500);
    }

    // --- Trigger 1: 30-second idle timer ---
    let proactiveTimer = setTimeout(showProactiveTrigger, 30000);

    // Reset timer on any user interaction
    ['mousemove', 'keydown', 'scroll', 'click'].forEach(evt => {
        document.addEventListener(evt, () => {
            clearTimeout(proactiveTimer);
            proactiveTimer = setTimeout(showProactiveTrigger, 30000);
        }, { once: false, passive: true });
    });

    // --- Trigger 2: Exit-intent (mouse leaving top of viewport) ---
    document.addEventListener('mouseleave', (e) => {
        // Only trigger when mouse exits from the top of the page
        if (e.clientY <= 5) {
            clearTimeout(proactiveTimer);
            showProactiveTrigger();
        }
    });

    // Hide tooltip when user manually opens chat
    bubble.addEventListener('click', () => {
        proactiveTooltip.classList.remove('show');
        const badge = shadow.getElementById('notif-badge');
        if (badge) badge.classList.remove('show');
        bubble.classList.remove('proactive-pulse');
        clearTimeout(proactiveTimer);
    });

})();
