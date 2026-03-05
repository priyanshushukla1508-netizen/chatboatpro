# ChatBotPro - Comprehensive Project Documentation

**Project Name:** ChatBotPro  
**Description:** AI Customer Support Chatbot Backend with Advanced Analytics and Lead Scoring  
**Version:** 0.0.1-SNAPSHOT  
**Java Version:** 17  
**Spring Boot Version:** 3.3.4  
**Last Updated:** March 4, 2026

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Technology Stack](#technology-stack)
3. [Project Architecture](#project-architecture)
4. [Database Schema & Entity Models](#database-schema--entity-models)
5. [API Endpoints](#api-endpoints)
6. [Service Layer](#service-layer)
7. [Security Implementation](#security-implementation)
8. [Frontend Structure](#frontend-structure)
9. [Key Features](#key-features)
10. [Configuration & Environment Variables](#configuration--environment-variables)
11. [Project Structure Details](#project-structure-details)
12. [Dependencies](#dependencies)
13. [Development Workflow](#development-workflow)

---

## Project Overview

**ChatBotPro** is an enterprise-grade AI-powered customer support chatbot platform designed to:

- **Provide Intelligent Chat Support:** Deliver AI-powered customer responses using Groq's LLaMA 3 models
- **Multi-turn Conversation Memory:** Maintain context across multiple exchanges for coherent conversations
- **Multilingual Support:** Automatic language detection with support for Hindi and English
- **Lead Scoring & Analytics:** Track visitor behavior and predict conversion likelihood (HOT/WARM/COLD)
- **Knowledge Gap Identification:** Identify questions the bot cannot answer to improve training
- **Objection Handling:** Pre-defined objection responses with multi-language support
- **Auto-FAQ Generation:** Generate FAQ answers from website content
- **Customer Satisfaction (CSAT) Tracking:** Collect post-chat ratings and feedback
- **Product Recommendations:** AI-driven product suggestions based on conversation context
- **Exit Intent Offers:** Coupon/discount display to prevent customer dropout
- **Follow-up Email Generation:** AI-generated personalized follow-up emails after conversations
- **Web Crawling & Content Scraping:** Native Java JSoup-based website content extraction
- **Dashboard Analytics:** Comprehensive business intelligence for chat interactions
- **Bot Token Authentication:** Secure API access via unique bot tokens
- **JWT-based User Authentication:** Secure user login and registration

---

## Technology Stack

### Backend
| Component | Technology | Version |
|-----------|-----------|---------|
| **Framework** | Spring Boot | 3.3.4 |
| **Language** | Java | 17 |
| **Database** | PostgreSQL (Supabase) | Latest |
| **ORM** | Spring Data JPA / Hibernate | 6.x |
| **Authentication** | Spring Security + JWT | 3.x / 0.11.5 |
| **AI Integration** | Groq API (LLaMA 3) | - |
| **Web Scraping** | JSoup | 1.17.2 |
| **PDF Processing** | Apache PDFBox | 3.0.3 |
| **Email** | Spring Mail | - |
| **Build Tool** | Maven | 3.x |
| **Logging** | SLF4J + Logback | - |
| **Project Lombok** | Code Generation | Latest |

### Frontend
| Component | Technology |
|-----------|-----------|
| **Structure** | HTML5 |
| **Styling** | CSS3 with Custom Properties |
| **Scripting** | Vanilla JavaScript |
| **Shadow DOM** | Native Web Components |
| **Storage** | LocalStorage API |
| **Communication** | REST API calls (Fetch API) |

### Infrastructure
| Component | Details |
|-----------|---------|
| **Database Host** | Supabase (PostgreSQL) |
| **AI Provider** | Groq API |
| **Deployment** | Render (Default URL in config) |
| **Port** | 8080 (Default) |

---

## Project Architecture

### High-Level Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    ChatBotPro System                         │
└─────────────────────────────────────────────────────────────┘

┌──────────────────┐
│   Frontend       │
│  - Widget (JS)   │
│  - Dashboard     │
│  - Login/Reg     │
└────────┬─────────┘
         │ HTTP/REST
         ▼
┌──────────────────────────────────────┐
│   Spring Boot Backend (Port 8080)    │
├──────────────────────────────────────┤
│                                      │
│  ┌────────────────────────────────┐  │
│  │   Controllers (REST APIs)      │  │
│  │ - AuthController               │  │
│  │ - WidgetController             │  │
│  │ - DashboardController          │  │
│  │ - FAQController                │  │
│  │ - WebsiteController            │  │
│  │ - AdvancedWidgetController     │  │
│  │ - AutoFAQController            │  │
│  │ - WidgetFeaturesController     │  │
│  └────────────────────────────────┘  │
│                 ▲                     │
│                 │                     │
│  ┌────────────────────────────────┐  │
│  │   Service Layer (Business      │  │
│  │   Logic & Orchestration)       │  │
│  │ - ChatService                  │  │
│  │ - AuthService                  │  │
│  │ - LeadScoringService           │  │
│  │ - FAQService                   │  │
│  │ - KnowledgeGapService          │  │
│  │ - WebsiteService               │  │
│  │ - WebCrawlerService            │  │
│  │ - AutoFAQService               │  │
│  │ - LeadScoringService           │  │
│  │ - CSATService                  │  │
│  │ - FollowUpEmailService         │  │
│  │ - ProductRecommendationService │  │
│  │ - ObjectionHandlerService      │  │
│  │ - LanguageDetectionService     │  │
│  │ - CouponService                │  │
│  │ - DashboardService             │  │
│  │ - CRMService                   │  │
│  │ - ImageAnalysisService         │  │
│  └────────────────────────────────┘  │
│                 ▲                     │
│                 │                     │
│  ┌────────────────────────────────┐  │
│  │   Repository Layer (Data)      │  │
│  │ - UserRepository               │  │
│  │ - WebsiteRepository            │  │
│  │ - ChatMessageRepository        │  │
│  │ - ChatSessionRepository        │  │
│  │ - FAQRepository                │  │
│  │ - LeadScoreRepository          │  │
│  │ - KnowledgeGapRepository       │  │
│  │ - CSATRatingRepository         │  │
│  │ - ProductRepository            │  │
│  │ - CouponRepository             │  │
│  │ - FAQSuggestionRepository      │  │
│  └────────────────────────────────┘  │
│                 ▲                     │
│                 │                     │
│  ┌────────────────────────────────┐  │
│  │   Security Layer               │  │
│  │ - SecurityConfig               │  │
│  │ - JwtAuthenticationFilter      │  │
│  │ - JwtUtils                     │  │
│  │ - ApplicationConfig            │  │
│  └────────────────────────────────┘  │
└──────────┬───────────────────────────┘
           │
           ├─────────────────────┬──────────────────┬──────────────┐
           ▼                     ▼                  ▼              ▼
    ┌──────────────┐      ┌──────────────┐  ┌──────────────┐ ┌─────────┐
    │  PostgreSQL  │      │   Groq AI    │  │  JSoup Web   │ │  Email  │
    │  (Supabase)  │      │     API      │  │   Crawler    │ │ (SMTP)  │
    └──────────────┘      └──────────────┘  └──────────────┘ └─────────┘

```

### Layered Architecture
- **Controller Layer:** REST endpoints handling HTTP requests
- **Service Layer:** Business logic, orchestration, external service integration
- **Repository Layer:** Data access using Spring Data JPA
- **Entity Layer:** JPA entities representing database tables
- **Security Layer:** JWT authentication, CORS, authorization
- **Client Layer:** External API integration (Groq)

---

## Database Schema & Entity Models

### Entity Relationship Overview

```
User (1) ──────────────────────(M) Website
         │                           │
         │                           ├─(1:M)─→ ChatSession
         │                           ├─(1:M)─→ ChatMessage
         │                           ├─(1:M)─→ FAQ
         │                           ├─(1:M)─→ Product
         │                           ├─(1:M)─→ Coupon
         │                           ├─(1:M)─→ LeadScore
         │                           ├─(1:M)─→ KnowledgeGap
         │                           ├─(1:M)─→ CSATRating
         │                           └─(1:M)─→ FAQSuggestion
```

### Detailed Entity Specifications

#### 1. **User Entity**
**Table Name:** `users`  
**Primary Key:** `id` (auto-increment)

| Column | Type | Constraints | Description |
|--------|------|-----------|-------------|
| id | BIGINT | PK, Auto-increment | Unique user identifier |
| email | VARCHAR | UNIQUE, NOT NULL | User email address (login credential) |
| password | VARCHAR | NOT NULL | BCrypt-encoded password |
| fullName | VARCHAR | Nullable | User's full name |
| createdAt | TIMESTAMP | NOT NULL | User registration timestamp |

**Relationships:**
- One-to-Many with `Website` (owner of websites)
- Timestamp: `@PrePersist` sets `createdAt` at creation

---

#### 2. **Website Entity**
**Table Name:** `websites`  
**Primary Key:** `id` (auto-increment)

| Column | Type | Constraints | Description |
|--------|------|-----------|-------------|
| id | BIGINT | PK, Auto-increment | Unique website identifier |
| userId | BIGINT | FK, NOT NULL | Reference to owner User |
| domain | VARCHAR | NOT NULL | Website domain/URL |
| name | VARCHAR | NOT NULL | Display name for website |
| botName | VARCHAR | Nullable | Custom chatbot name |
| welcomeMessage | TEXT | Nullable | Custom welcome greeting |
| primaryColor | VARCHAR | Nullable | Hex color for widget branding |
| botToken | VARCHAR | UNIQUE, NOT NULL | Secret token for widget authentication |
| plan | VARCHAR | Default: "TRIAL" | Subscription tier (TRIAL, PRO, ENTERPRISE) |
| active | BOOLEAN | Default: true | Whether the bot is active |
| lastCrawledAt | TIMESTAMP | Nullable | Last website scrape timestamp |
| createdAt | TIMESTAMP | NOT NULL | Website creation timestamp |

**Relationships:**
- Many-to-One with `User` (owner)
- One-to-Many with: `ChatSession`, `ChatMessage`, `FAQ`, `Product`, `Coupon`, `LeadScore`, `KnowledgeGap`, `CSATRating`, `FAQSuggestion`
- Bot Token: Auto-generated UUID if not provided

---

#### 3. **ChatSession Entity**
**Table Name:** `chat_sessions`  
**Primary Key:** `id` (auto-increment)

| Column | Type | Constraints | Description |
|--------|------|-----------|-------------|
| id | BIGINT | PK, Auto-increment | Unique session identifier |
| websiteId | BIGINT | FK, NOT NULL | Parent website |
| sessionId | VARCHAR | Nullable | Unique session identifier (usually UUID) |
| visitorName | VARCHAR | Nullable | Visitor's name (if provided) |
| visitorEmail | VARCHAR | Nullable | Visitor's email (for follow-ups) |
| conversationSummary | TEXT | Nullable | AI-generated summary of entire conversation |
| followUpEmailBody | TEXT | Nullable | AI-generated follow-up email content |
| emailSent | BOOLEAN | Default: false | Whether follow-up email was sent |
| startedAt | TIMESTAMP | NOT NULL | Session start timestamp |
| endedAt | TIMESTAMP | Nullable | Session end timestamp |

**Relationships:**
- Many-to-One with `Website`
- Implicit One-to-Many with `ChatMessage` (via sessionId)

---

#### 4. **ChatMessage Entity**
**Table Name:** `chat_messages`  
**Primary Key:** `id` (auto-increment)

| Column | Type | Constraints | Description |
|--------|------|-----------|-------------|
| id | BIGINT | PK, Auto-increment | Unique message identifier |
| websiteId | BIGINT | FK, NOT NULL | Parent website |
| sessionId | VARCHAR | NOT NULL | Session reference |
| message | VARCHAR | NOT NULL, 2000 char limit | Message content |
| senderType | VARCHAR | NOT NULL | "VISITOR", "BOT", "ADMIN", or "INTERNAL_NOTE" |
| createdAt | TIMESTAMP | NOT NULL | Message creation timestamp |

**Relationships:**
- Many-to-One with `Website`
- Part of `ChatSession` (via sessionId)

---

#### 5. **FAQ Entity**
**Table Name:** `faqs`  
**Primary Key:** `id` (auto-increment)

| Column | Type | Constraints | Description |
|--------|------|-----------|-------------|
| id | BIGINT | PK, Auto-increment | Unique FAQ identifier |
| websiteId | BIGINT | FK, NOT NULL | Parent website |
| question | VARCHAR | NOT NULL, 1000 char limit | FAQ question |
| answer | VARCHAR | NOT NULL, 2000 char limit | FAQ answer (context for AI) |
| isAIGenerated | BOOLEAN | Default: false | Whether AI auto-generated the answer |
| createdAt | TIMESTAMP | NOT NULL | FAQ creation timestamp |

**Relationships:**
- Many-to-One with `Website`

---

#### 6. **Product Entity**
**Table Name:** `products`  
**Primary Key:** `id` (auto-increment)

| Column | Type | Constraints | Description |
|--------|------|-----------|-------------|
| id | BIGINT | PK, Auto-increment | Unique product identifier |
| websiteId | BIGINT | FK, NOT NULL | Parent website |
| name | VARCHAR | NOT NULL | Product name |
| description | TEXT | Nullable | Product description |
| imageUrl | VARCHAR | Nullable | Product image URL |
| price | DECIMAL | Nullable | Product price |
| buyLink | VARCHAR | Nullable | URL to purchase product |
| keywords | TEXT | Nullable | CSV: "shoes, sneaker, footwear" |
| createdAt | TIMESTAMP | NOT NULL | Product creation timestamp |

**Relationships:**
- Many-to-One with `Website`
- Used by ProductRecommendationService for keyword matching

---

#### 7. **LeadScore Entity**
**Table Name:** `lead_scores`  
**Primary Key:** `id` (auto-increment)

| Column | Type | Constraints | Description |
|--------|------|-----------|-------------|
| id | BIGINT | PK, Auto-increment | Unique lead identifier |
| websiteId | BIGINT | FK, Nullable | Parent website |
| sessionId | VARCHAR | Nullable | Session reference |
| visitorName | VARCHAR | Nullable | Visitor name |
| visitorEmail | VARCHAR | Nullable | Visitor email |
| visitorPhone | VARCHAR | Nullable | Visitor phone |
| timeOnPageSeconds | INTEGER | Nullable | Total time spent on page |
| messageCount | INTEGER | Nullable | Number of messages sent |
| pagesVisited | INTEGER | Nullable | Number of pages visited |
| visitedPricing | BOOLEAN | Nullable | Whether visited pricing page |
| askedAboutEnterprise | BOOLEAN | Nullable | Enterprise interest signal |
| askedPrice | BOOLEAN | Nullable | Price inquiry signal |
| score | INTEGER | Nullable | Computed score (0-100) |
| tier | VARCHAR | Nullable | "HOT", "WARM", or "COLD" |
| alertSent | BOOLEAN | Default: false | Alert email sent status |
| createdAt | TIMESTAMP | NOT NULL | Record creation timestamp |
| updatedAt | TIMESTAMP | NOT NULL | Last update timestamp |

**Scoring Algorithm:**
- Time on page: max 20 pts (2 pts per 30s)
- Messages: max 20 pts (4 pts each)
- Pages visited: max 15 pts (5 pts each)
- Pricing visit: +15 pts
- Asked price: +15 pts
- Asked enterprise: +15 pts
- **Tiers:** HOT (61-100), WARM (31-60), COLD (0-30)

---

#### 8. **KnowledgeGap Entity**
**Table Name:** `knowledge_gaps`  
**Primary Key:** `id` (auto-increment)

| Column | Type | Constraints | Description |
|--------|------|-----------|-------------|
| id | BIGINT | PK, Auto-increment | Unique knowledge gap identifier |
| websiteId | BIGINT | FK, NOT NULL | Parent website |
| question | VARCHAR | NOT NULL, 1000 char limit | Unanswered question |
| occurrenceCount | INTEGER | Nullable | How many times this was asked |
| resolved | BOOLEAN | Default: false | Whether owner provided answer |
| suggestedAnswer | TEXT | Nullable | Owner's provided answer |
| firstSeenAt | TIMESTAMP | NOT NULL | First occurrence timestamp |
| lastSeenAt | TIMESTAMP | NOT NULL | Last occurrence timestamp |

**Relationships:**
- Many-to-One with `Website`
- Used for identifying training gaps in the AI model

---

#### 9. **CSATRating Entity**
**Table Name:** `csat_ratings`  
**Primary Key:** `id` (auto-increment)

| Column | Type | Constraints | Description |
|--------|------|-----------|-------------|
| id | BIGINT | PK, Auto-increment | Unique CSAT record identifier |
| websiteId | BIGINT | FK, NOT NULL | Parent website |
| sessionId | VARCHAR | Nullable | Session reference |
| rating | INTEGER | Nullable | 1-5 star rating |
| feedback | TEXT | Nullable | Optional feedback text |
| createdAt | TIMESTAMP | NOT NULL | Rating timestamp |

**Relationships:**
- Many-to-One with `Website`

---

#### 10. **Coupon Entity**
**Table Name:** `coupons`  
**Primary Key:** `id` (auto-increment)

| Column | Type | Constraints | Description |
|--------|------|-----------|-------------|
| id | BIGINT | PK, Auto-increment | Unique coupon identifier |
| websiteId | BIGINT | FK, NOT NULL | Parent website |
| code | VARCHAR | NOT NULL | Coupon code (e.g., "SAVE10") |
| discountPercent | INTEGER | NOT NULL | Discount percentage (e.g., 10) |
| message | TEXT | Nullable | Default: "Wait! Use code {code} for {discount}% off!" |
| timerSeconds | INTEGER | Default: 300 | Countdown timer (5 minutes) |
| active | BOOLEAN | Default: true | Whether coupon is active |
| createdAt | TIMESTAMP | NOT NULL | Coupon creation timestamp |

**Relationships:**
- Many-to-One with `Website`
- Displayed as exit modal before visitor leaves

---

#### 11. **FAQSuggestion Entity**
**Table Name:** `faq_suggestions`  
**Primary Key:** `id` (auto-increment)

| Column | Type | Constraints | Description |
|--------|------|-----------|-------------|
| id | BIGINT | PK, Auto-increment | Unique suggestion identifier |
| websiteId | BIGINT | FK, NOT NULL | Parent website |
| question | TEXT | NOT NULL | Suggested Q&A question |
| answer | TEXT | NOT NULL | Suggested Q&A answer |
| status | ENUM | Default: "PENDING" | "PENDING", "APPROVED", "REJECTED" |
| createdAt | TIMESTAMP | NOT NULL | Creation timestamp |

**Relationships:**
- Many-to-One with `Website`
- Status workflow: PENDING → APPROVED/REJECTED

---

## API Endpoints

### Authentication Endpoints

#### Register User
**Endpoint:** `POST /api/v1/auth/register`  
**Description:** Create new user account  
**Authentication:** None (public)  
**Request Body:**
```json
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "SecurePassword123"
}
```
**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "john@example.com",
  "fullName": "John Doe"
}
```

#### Login User
**Endpoint:** `POST /api/v1/auth/authenticate`  
**Description:** Authenticate and get JWT token  
**Authentication:** None (public)  
**Request Body:**
```json
{
  "email": "john@example.com",
  "password": "SecurePassword123"
}
```
**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "john@example.com",
  "fullName": "John Doe"
}
```

---

### Widget Endpoints (Public)

#### Get Bot Configuration
**Endpoint:** `GET /api/v1/widget/config/{botToken}`  
**Description:** Get website config for widget initialization  
**Authentication:** None (public, bot token based)  
**Parameters:**
- `botToken` (path): Unique website identifier
**Response:**
```json
{
  "id": 1,
  "name": "Acme Corp",
  "domain": "acme.com",
  "botName": "Acme Assistant",
  "welcomeMessage": "Hi! How can I help?",
  "primaryColor": "#6C63FF"
}
```

#### Send Chat Message
**Endpoint:** `POST /api/v1/widget/chat`  
**Description:** Send visitor message & get AI response  
**Authentication:** None (public)  
**Request Body:**
```json
{
  "botToken": "unique-bot-token",
  "sessionId": "session-123",
  "message": "What are your shipping rates?"
}
```
**Response:**
```json
{
  "response": "We offer free shipping on orders over $50...",
  "sessionId": "session-123",
  "createdAt": "2026-03-04T10:30:00"
}
```

#### Get Chat History
**Endpoint:** `GET /api/v1/widget/chat/{sessionId}`  
**Description:** Retrieve previous messages in a session  
**Authentication:** None (public)  
**Parameters:**
- `sessionId` (path): Session identifier
**Response:**
```json
[
  {
    "id": 1,
    "sessionId": "session-123",
    "message": "Hi, I need help",
    "senderType": "VISITOR",
    "createdAt": "2026-03-04T10:25:00"
  },
  {
    "id": 2,
    "sessionId": "session-123",
    "message": "Hello! How can I assist?",
    "senderType": "BOT",
    "createdAt": "2026-03-04T10:25:30"
  }
]
```

---

### Website Management Endpoints

#### Create Website
**Endpoint:** `POST /api/v1/websites`  
**Description:** Create new bot-enabled website  
**Authentication:** JWT Token  
**Request Body:**
```json
{
  "domain": "example.com",
  "name": "Example Corp",
  "botName": "ExampleBot",
  "welcomeMessage": "Welcome!",
  "primaryColor": "#FF6B9D"
}
```
**Response:**
```json
{
  "id": 1,
  "domain": "example.com",
  "name": "Example Corp",
  "botToken": "550e8400-e29b-41d4-a716-446655440000",
  "plan": "TRIAL",
  "active": true,
  "createdAt": "2026-03-04T10:00:00"
}
```

#### Get All Websites
**Endpoint:** `GET /api/v1/websites`  
**Description:** List all websites for authenticated user  
**Authentication:** JWT Token  
**Response:**
```json
[
  {
    "id": 1,
    "domain": "example.com",
    "name": "Example Corp",
    "botName": "ExampleBot",
    "botToken": "550e8400-e29b-41d4-a716-446655440000",
    "plan": "TRIAL",
    "active": true
  }
]
```

#### Trigger Crawl
**Endpoint:** `POST /api/v1/websites/{id}/crawl`  
**Description:** Start website content scraping  
**Authentication:** JWT Token  
**Response:** `202 Accepted`

---

### FAQ Management Endpoints

#### Get FAQs for Website
**Endpoint:** `GET /api/v1/faqs/website/{websiteId}`  
**Description:** List all FAQs for a website  
**Authentication:** JWT Token  
**Response:**
```json
[
  {
    "id": 1,
    "question": "What is your return policy?",
    "answer": "30-day money-back guarantee...",
    "isAIGenerated": false,
    "createdAt": "2026-03-04T09:00:00"
  }
]
```

#### Create FAQ
**Endpoint:** `POST /api/v1/faqs`  
**Description:** Add new FAQ manually  
**Authentication:** JWT Token  
**Request Body:**
```json
{
  "websiteId": 1,
  "question": "Do you offer bulk discounts?",
  "answer": "Yes, we offer volume discounts..."
}
```
**Response:**
```json
{
  "id": 2,
  "question": "Do you offer bulk discounts?",
  "answer": "Yes, we offer volume discounts...",
  "isAIGenerated": false
}
```

#### Delete FAQ
**Endpoint:** `DELETE /api/v1/faqs/{id}`  
**Description:** Remove FAQ  
**Authentication:** JWT Token  
**Response:** `204 No Content`

#### Upload Document/PDF
**Endpoint:** `POST /api/v1/faqs/upload/{websiteId}`  
**Description:** Extract content from PDF and create FAQs  
**Authentication:** JWT Token  
**Parameters:**
- `file` (multipart): PDF file
**Response:**
```json
{
  "count": 5,
  "message": "5 FAQs extracted from document"
}
```

---

### Dashboard Endpoints

#### Get Dashboard Statistics
**Endpoint:** `GET /api/v1/dashboard/stats`  
**Description:** Get overview metrics (messages, satisfaction, leads)  
**Authentication:** JWT Token  
**Response:**
```json
{
  "totalMessages": 1250,
  "totalSessions": 340,
  "avgCSAT": 4.2,
  "hotLeads": 25,
  "warmLeads": 45,
  "totalConversions": 12
}
```

#### Get Live Sessions
**Endpoint:** `GET /api/v1/dashboard/sessions/{websiteId}`  
**Description:** Get active chat sessions  
**Authentication:** JWT Token  
**Response:**
```json
[
  {
    "sessionId": "session-001",
    "needsHuman": false
  },
  {
    "sessionId": "session-002",
    "needsHuman": true
  }
]
```

#### Get Chat History
**Endpoint:** `GET /api/v1/dashboard/chat/{sessionId}`  
**Description:** Retrieve full conversation  
**Authentication:** JWT Token  
**Response:** Array of ChatMessage objects

#### Send Admin Response
**Endpoint:** `POST /api/v1/dashboard/chat/send`  
**Description:** Send human agent message to visitor  
**Authentication:** JWT Token  
**Request Body:**
```json
{
  "sessionId": "session-001",
  "websiteId": 1,
  "message": "A human agent has joined the chat..."
}
```

#### Send Internal Note
**Endpoint:** `POST /api/v1/dashboard/chat/note`  
**Description:** Add internal note (not visible to visitor)  
**Authentication:** JWT Token  
**Request Body:**
```json
{
  "sessionId": "session-001",
  "websiteId": 1,
  "note": "This customer is asking about our enterprise plan"
}
```

#### Get Knowledge Gaps
**Endpoint:** `GET /api/v1/dashboard/gaps/{websiteId}`  
**Description:** List unresolved knowledge gaps  
**Authentication:** JWT Token  
**Response:**
```json
[
  {
    "id": 1,
    "question": "Do you have API documentation?",
    "occurrenceCount": 3,
    "resolved": false,
    "firstSeenAt": "2026-03-01T10:00:00"
  }
]
```

#### Resolve Knowledge Gap
**Endpoint:** `POST /api/v1/dashboard/gaps/{gapId}/resolve`  
**Description:** Mark gap as resolved and provide answer  
**Authentication:** JWT Token  
**Request Body:**
```json
{
  "answer": "Yes, API docs are at docs.example.com"
}
```

---

## Service Layer

### Core Services Overview

#### 1. **ChatService**
**Location:** `com.chatbotpro.service.ChatService`  
**Responsibilities:**
- Process incoming visitor messages
- Build AI prompt with FAQ and website content context
- Handle multi-turn conversation memory (last 8 messages)
- Integrate with Groq AI API for response generation
- Detect and handle objections
- Implement trial plan slowdown (150+ messages)
- Store messages in database

**Key Methods:**
- `ChatResponse getChatResponse(ChatRequest)` - Main chat processing
- `List<ChatMessage> getChatHistoryForWidget(String sessionId)` - Retrieve history

**Integration Points:**
- GroqClient (AI responses)
- FAQRepository (knowledge base)
- ScrapedContentService (website context)
- ObjectionHandlerService (predefined responses)
- LanguageDetectionService (multilingual support)

---

#### 2. **AuthService**
**Location:** `com.chatbotpro.service.AuthService`  
**Responsibilities:**
- User registration with password hashing
- User authentication and login
- JWT token generation
- User validation

**Key Methods:**
- `AuthResponse register(RegisterRequest)` - Create new user
- `AuthResponse login(LoginRequest)` - Authenticate user

**Security Features:**
- BCrypt password encoding
- JWT token with 24-hour expiration
- Email uniqueness validation

---

#### 3. **WebsiteService**
**Location:** `com.chatbotpro.service.WebsiteService`  
**Responsibilities:**
- Create and manage websites
- Generate unique bot tokens
- Retrieve website configuration
- Trigger web crawling

**Key Methods:**
- `WebsiteResponse createWebsite(WebsiteRequest)`
- `WebsiteResponse getByToken(String token)` - Public access
- `void triggerCrawl(Long id, String domain)`

---

#### 4. **LeadScoringService**
**Location:** `com.chatbotpro.service.LeadScoringService`  
**Responsibilities:**
- Calculate lead scores based on behavioral signals
- Update scores in real-time
- Assign lead tiers (HOT/WARM/COLD)
- Provide lead analytics

**Scoring Algorithm:**
```
Time on page:        0-20 pts (2 pts per 30s)
Messages sent:       0-20 pts (4 pts per message)
Pages visited:       0-15 pts (5 pts per page)
Visited pricing:     +15 pts
Asked price:         +15 pts
Asked enterprise:    +15 pts
────────────────────
Total Score:         0-100 pts
```

**Key Methods:**
- `LeadScore updateScore(String botToken, String sessionId, Map behavior)`
- `List<LeadScore> getLeadsForWebsite(Long websiteId)`

---

#### 5. **FAQService**
**Location:** `com.chatbotpro.service.FAQService`  
**Responsibilities:**
- Manage FAQ Q&A pairs
- Extract content from uploaded documents
- Generate FAQ suggestions
- Provide knowledge base context

**Key Methods:**
- `List<FAQResponse> getFAQsByWebsite(Long websiteId)`
- `FAQResponse createFAQ(FAQRequest)`
- `Integer uploadDocument(Long websiteId, MultipartFile file)` - PDF extraction

---

#### 6. **KnowledgeGapService**
**Location:** `com.chatbotpro.service.KnowledgeGapService`  
**Responsibilities:**
- Identify unanswered questions
- Track question frequency
- Allow owners to provide answers
- Mark gaps as resolved

**Key Methods:**
- `List<KnowledgeGap> getUnresolvedGaps(Long websiteId)`
- `void resolveGap(Long gapId, String answer)`

---

#### 7. **WebCrawlerService**
**Location:** `com.chatbotpro.service.WebCrawlerService`  
**Responsibilities:**
- Scrape website content using JSoup
- Extract clean text from HTML
- Remove noise (scripts, styles, nav, footer)
- Enforce token limits (15,000 chars max)

**Key Features:**
- Mozilla User-Agent to bypass basic bot detection
- 30-second timeout per request
- Automatic cleanup of non-content elements
- Token-aware text truncation

---

#### 8. **LanguageDetectionService**
**Location:** `com.chatbotpro.service.LanguageDetectionService`  
**Responsibilities:**
- Detect language of messages (Hindi/English)
- Provide language-specific prompts
- Handle multilingual responses

**Supported Languages:**
- English
- Hindi

---

#### 9. **ObjectionHandlerService**
**Location:** `com.chatbotpro.service.ObjectionHandlerService`  
**Responsibilities:**
- Pre-identify common objections
- Return predefined multilingual responses
- Skip expensive AI calls for handled objections

**Examples:**
- "too expensive" / "बहुत महंगा है"
- "not interested" / "मुझे दिलचस्पी नहीं है"

---

#### 10. **FollowUpEmailService**
**Location:** `com.chatbotpro.service.FollowUpEmailService`  
**Responsibilities:**
- Generate personalized follow-up emails
- Insert conversation summaries
- Send emails via SMTP
- Track email delivery

**Features:**
- AI-generated personalized content
- HTML email templates
- Retry logic for failed sends

---

#### 11. **ProductRecommendationService**
**Location:** `com.chatbotpro.service.ProductRecommendationService`  
**Responsibilities:**
- Match conversation keywords with products
- Suggest relevant products to visitors
- Provide product context to AI

**Matching Algorithm:**
- Keyword-based matching
- Case-insensitive comparison
- Multiple keyword support (CSV format)

---

#### 12. **CSATService**
**Location:** `com.chatbotpro.service.CSATService`  
**Responsibilities:**
- Record post-chat satisfaction ratings
- Aggregate CSAT metrics
- Generate satisfaction reports

**Rating Scale:** 1-5 stars

---

#### 13. **DashboardService**
**Location:** `com.chatbotpro.service.DashboardService`  
**Responsibilities:**
- Aggregate dashboard metrics
- Calculate statistics
- Provide live inbox functionality
- Handle admin responses

**Key Methods:**
- `DashboardResponse getStats()`
- `ChatMessage sendAdminResponse(...)`
- `ChatMessage sendInternalNote(...)`

---

#### 14. **AutoFAQService**
**Location:** `com.chatbotpro.service.AutoFAQService`  
**Responsibilities:**
- Generate FAQ suggestions from website content
- AI-powered Q&A pair creation
- Workflow for FAQ approval

---

#### 15. **ScrapedContentService**
**Location:** `com.chatbotpro.service.ScrapedContentService`  
**Responsibilities:**
- Store scraped website content
- Provide content for AI context
- Cache management

---

#### 16. **CRMService**
**Location:** `com.chatbotpro.service.CRMService`  
**Responsibilities:**
- CRM integration hooks
- Lead persistence
- Sales workflow integration

---

#### 17. **ImageAnalysisService**
**Location:** `com.chatbotpro.service.ImageAnalysisService`  
**Responsibilities:**
- Analyze images uploaded in chat
- Extract text from images (OCR potential)
- Provide image context to AI

---

#### 18. **CouponService**
**Location:** `com.chatbotpro.service.CouponService`  
**Responsibilities:**
- Manage exit-intent coupons
- Generate dynamic messages
- Track coupon usage

---

## Security Implementation

### JWT Authentication Flow

```
┌─────────────┐
│   Browser   │
└────┬────────┘
     │ 1. POST /api/v1/auth/authenticate
     ▼
┌─────────────────────────┐
│   AuthController        │
│  (login endpoint)       │
└────┬────────────────────┘
     │ 2. Verify credentials
     ▼
┌─────────────────────────┐
│   AuthService           │
│  (password check)       │
└────┬────────────────────┘
     │ 3. Generate JWT
     ▼
┌─────────────────────────┐
│   JwtUtils              │
│  (token creation)       │
└────┬────────────────────┘
     │ 4. Return token
     ▼
┌─────────────┐
│   Browser   │ Stores token in localStorage
└────┬────────┘
     │ 5. Subsequent requests with Authorization header
     ▼
┌─────────────────────────┐
│   JwtAuthenticationFilter╱
│   (intercepts request)  │
└────┬────────────────────┘
     │ 6. Extract & validate token
     ▼
┌─────────────────────────┐
│   JwtUtils              │
│  (token validation)     │
└────┬────────────────────┘
     │ 7. Allow/Deny request
     ▼
┌─────────────────────────┐
│   Protected Controller  │
└─────────────────────────┘
```

### Security Components

#### 1. **SecurityConfig**
**Location:** `com.chatbotpro.security.SecurityConfig`

**Configuration Details:**
- CSRF disabled (stateless API)
- CORS enabled for all origins (widget embeddability)
- Session management: STATELESS (no server sessions)
- JWT filter added before UsernamePasswordAuthenticationFilter
- Public endpoints: `/api/v1/auth/**`, `/api/v1/widget/**`
- Protected endpoints: All others require JWT token

**CORS Configuration:**
```
Allowed Origins: * (all)
Allowed Methods: GET, POST, PUT, DELETE, OPTIONS
Allowed Headers: Authorization, Content-Type, X-Requested-With
```

---

#### 2. **JwtUtils**
**Location:** `com.chatbotpro.security.JwtUtils`

**Token Configuration:**
- Algorithm: HS256 (HMAC SHA256)
- Secret: Configurable (from `jwt.secret` property)
- Expiration: 86,400,000 ms (24 hours)
- Claims: Username (subject)

**Key Methods:**
- `String generateToken(UserDetails)` - Create JWT
- `Boolean validateToken(String token, UserDetails)` - Verify token
- `String extractUsername(String token)` - Get username from token
- `Date extractExpiration(String token)` - Get expiration time

**JWT Structure:**
```
Header.Payload.Signature

Example:
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.
eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNjcyNzQ4MDAwLCJleHAiOjE2NzI4MzQ0MDB9.
Vf3Mle8u1kF5sKqQ6n7TlVq4vB9cA0xY2pZ9qR1sT8w
```

---

#### 3. **JwtAuthenticationFilter**
**Location:** `com.chatbotpro.security.JwtAuthenticationFilter`

**Responsibilities:**
- Intercept incoming HTTP requests
- Extract JWT token from Authorization header
- Validate token integrity and expiration
- Set authentication in Spring Security context
- Chain to next filter if validation passes
- Allow unauthenticated access to public endpoints

**Header Format:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

#### 4. **ApplicationConfig**
**Location:** `com.chatbotpro.security.ApplicationConfig`

**Beans Provided:**
- `AuthenticationProvider` - Handles credential validation
- `PasswordEncoder` (BCrypt) - Secure password hashing
- `RestTemplate` - HTTP client for external APIs

---

### Password Security

**Algorithm:** BCrypt (Spring Security PasswordEncoder)  
**Salt:** Auto-generated per password  
**Strength:** 10 iterations (default)  
**Protection:** Resistant to brute-force and dictionary attacks

---

### API Key Security

| Credential | Storage | Exposure | Rotation |
|-----------|---------|----------|----------|
| JWT Secret | Environment variable `JWT_SECRET` | Low (server-side only) | Manual |
| Groq API Key | Environment variable `GROQ_API_KEY` | Low (server-side only) | Via Groq dashboard |
| Database Password | Environment variable `DB_PASS` | Low (server-side only) | Via Supabase |
| Bot Token | Database | Medium (transmitted to widget) | Regenerable per website |

---

## Frontend Structure

### HTML Pages Overview

#### 1. **index.html** - Main Dashboard
**Purpose:** User dashboard and management interface  
**Features:**
- Website list and management
- Chat session monitoring
- Analytics overview
- Navigation to other pages
- JWT token validation

#### 2. **login.html** - Login Page
**Purpose:** User authentication  
**Features:**
- Email and password input
- Error handling & display
- Redirect to dashboard on success
- Link to registration page
- Dark theme with primary color (#6C63FF)

#### 3. **register.html** - Registration Page
**Purpose:** New user account creation  
**Features:**
- Full name, email, password inputs
- Password confirmation validation
- Error display
- Redirect to dashboard on success
- Link to login page

#### 4. **dashboard.html** - Advanced Dashboard
**Purpose:** Detailed analytics and reporting  
**Features:**
- Message statistics
- CSAT metrics
- Lead conversion tracking
- Performance graphs
- Export functionality

#### 5. **heatmap.html** - Visitor Heatmap
**Purpose:** Visualize visitor interaction patterns  
**Features:**
- Page interaction heat visualization
- Click-through tracking
- Bounce analysis
- User flow diagrams

#### 6. **knowledge-gaps.html** - Training Interface
**Purpose:** Identify and resolve model gaps  
**Features:**
- Display unanswered questions
- Frequency tracking
- Owner can provide correct answers
- Mark as trained/resolved
- Bulk actions

#### 7. **templates.html** - Email Templates
**Purpose:** Manage follow-up email templates  
**Features:**
- Template WYSIWYG editor
- Preview functionality
- Variable insertion ({{name}}, {{order_id}})
- Template versioning

#### 8. **widget/** - Embedded Chat Widget
**Purpose:** Embeddable chatbot for websites  
**Files:**
- `widget.js` - Main widget logic (895 lines)
- Standalone JavaScript module
- Shadow DOM encapsulation
- Zero dependencies

---

### Widget Implementation Details (widget.js)

#### Widget Architecture

**Core Components:**
```javascript
1. Chat Bubble - Fixed bottom-right button with icon
2. Chat Window - Expandable conversation interface
3. Message History - Scrollable message list
4. Input Field - Message composition area
5. Header - Bot name and close button
```

#### Key Features

**Session Management:**
- Unique sessionId per visitor (localStorage-based)
- Persists across page reloads
- Stored in browser's localStorage

**Theme Customization:**
- Primary color configurable via data attribute
- CSS custom properties for dynamic theming
- Dark theme by default

**Shadow DOM Isolation:**
```javascript
const shadow = container.attachShadow({ mode: 'open' });
// Prevents CSS conflicts with host website
// Encapsulates styles and markup
```

**API Integration:**
```javascript
const API_BASE = "https://YOUR-BACKEND-URL.onrender.com/api/v1/widget";
// Fetch config: GET /config/{token}
// Send message: POST /chat
// Get history: GET /chat/{sessionId}
```

**User Interaction Events:**
- Bubble click to open/close
- Message send on button click or Enter key
- Typing indicators
- Auto-scroll to latest message

#### Widget Installation

**Embed Code:**
```html
<script 
  async 
  src="https://your-domain.com/widget/widget.js" 
  data-bot-id="unique-bot-token">
</script>
```

**Configuration:**
- `data-bot-id` - Required: Bot token from dashboard
- Automatically initializes on DOM ready
- No dependencies required
- Works with any framework

#### Styling Variables

```css
--primary-color:  #6C63FF              /* Main brand color */
--bg-color:       #0D0E1C              /* Window background */
--surface-color:  #161728              /* Header background */
--text-color:     #FFFFFF              /* Main text */
--text-muted:     #B0B3C6              /* Secondary text */
```

---

## Key Features

### Feature 1: Multi-turn Conversation Memory
- **Scope:** Stores last 8 messages (4 exchanges)
- **Purpose:** Provide context-aware responses
- **Implementation:** Retrieved from ChatMessageRepository
- **Benefit:** Natural conversational flow without repetition
- **Limitation:** Token budget constraints limit history depth

### Feature 2: Multilingual Auto-Detection
- **Supported Languages:** English, Hindi
- **Detection Method:** Character pattern analysis
- **Implementation:** LanguageDetectionService
- **Response:** Language-specific prompts injected into AI instruction
- **Benefit:** Seamless bilingual support without explicit user selection

### Feature 3: Lead Scoring & Predictive Analytics
- **Signals Tracked:**
  - Time on page (engagement indicator)
  - Message count (conversation depth)
  - Pages visited (site exploration)
  - Pricing page visit (buying intent)
  - Price inquiry (intent confirmation)
  - Enterprise question (high-value indicator)
- **Scoring Formula:** Algorithmic point system (0-100)
- **Tiers:** HOT (61-100), WARM (31-60), COLD (0-30)
- **Use Case:** Sales team prioritization
- **Limitation:** Trial plan limit at 150 messages

### Feature 4: Objection Handling
- **Approach:** Pre-defined responses for common objections
- **Languages:** Multilingual responses
- **Examples:**
  - "too expensive" → "We have flexible payment plans..."
  - "not interested" → "No pressure! But here's why..."
- **Benefit:** Faster response time, no AI call overhead
- **Database:** In-memory (ObjectionHandlerService)

### Feature 5: Knowledge Gap Detection
- **Mechanism:** Questions that AI cannot answer well are logged
- **Aggregation:** Grouped by frequency and marked as unresolved
- **Owner Action:** Can provide correct answer (training)
- **Benefit:** Continuous model improvement
- **Dashboard:** Accessible via `/dashboard/gaps/{websiteId}`

### Feature 6: Auto-FAQ Generation
- **Source:** Website content (via web crawler)
- **Method:** AI generates Q&A pairs from extracted text
- **Status Workflow:** PENDING → APPROVED/REJECTED
- **Use Case:** Rapid knowledge base bootstrapping
- **Benefit:** Reduces manual FAQ creation time

### Feature 7: Web Crawling & Content Extraction
- **Technology:** JSoup (native Java)
- **Scope:** Main page + linked pages
- **Cleanup:** Removes scripts, styles, nav elements
- **Token Limit:** 15,000 characters max (Groq context budget)
- **Speed:** ~30-second timeout per request
- **User-Agent:** Mozilla/5.0 (realistic browser string)
- **Use Case:** Populate AI knowledge base without manual uploads

### Feature 8: Exit-Intent Coupons
- **Trigger:** Exit-intent detection (mouse leave top of page)
- **Display:** Modal with countdown timer
- **Customization:** Coupon code, discount %, message
- **Timer:** 5 minutes (configurable)
- **Benefit:** Reduce bounce rate and capture emails
- **Database:** Coupon entity with active status

### Feature 9: Follow-up Email Generation
- **Trigger:** After chat session ends (visitor close chat)
- **Content:** AI-generated personalized email
- **Variables:** Visitor name, order info, product details
- **Channel:** SMTP (Gmail-compatible)
- **Status:** Tracked in ChatSession.emailSent
- **Benefit:** Continue engagement post-conversation

### Feature 10: Chat Sentiment Analysis (via ChatMessage types)
- **Message Types:** VISITOR, BOT, ADMIN, INTERNAL_NOTE
- **CSAT Tracking:** 1-5 star ratings
- **Feedback:** Optional text comments
- **Use Case:** Quality assurance and satisfaction trends

### Feature 11: Product Recommendation
- **Matching:** Keyword-based (CSV product keywords)
- **Context:** Product name, description, price, image
- **Trigger:** During conversation if relevance detected
- **Benefit:** Increase average order value (AOV)
- **Database:** Product entity with keywords field

### Feature 12: Live Inbox & Admin Response
- **Real-time:** View active visitor sessions
- **Admin Join:** Send message as human agent
- **Internal Notes:** Comments team can see (visitor-hidden)
- **Use Case:** Escalation and human takeover
- **Dashboard:** `/dashboard/sessions/{websiteId}`

### Feature 13: Trial Plan Rate Limiting
- **Limit:** 150 messages per trial
- **Effect:** After 150 messages, 5-second delay per response
- **Message:** Hindi-language nudge to upgrade
- **Purpose:** Encourage paid plan conversion
- **Database:** Implicit in message counting logic

---

## Configuration & Environment Variables

### Database Configuration

**File:** `application.properties`

#### PostgreSQL (Supabase Production)
```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://db.fltmfuntfpzlkwqfljus.supabase.co:5432/postgres}
spring.datasource.username=${DB_USER:postgres}
spring.datasource.password=${DB_PASS:riyajaan@Pk}
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
```

#### H2 In-Memory (Local Development - Commented Out)
```properties
# spring.datasource.url=jdbc:h2:mem:chatbotdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
# spring.datasource.driver-class-name=org.h2.Driver
# spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
# spring.h2.console.enabled=true
# spring.h2.console.path=/h2-console
```

---

### JWT Configuration

```properties
jwt.secret=${JWT_SECRET:NyijgiIRclNPuBTZ+eVB2ozKfFO318HtYX8AHRMKkN8f6qAiKzlss8qPpBLS2UmXdS+pjs14jSyEGAd+wf0arQ==}
jwt.expiration=86400000  # 24 hours in milliseconds
```

---

### Groq AI Configuration

```properties
groq.api.key=${GROQ_API_KEY}           # Required: From Groq dashboard
groq.model=llama3-8b-8192              # Model identifier
ollama.base.url=${OLLAMA_URL:http://localhost:11434}
```

---

### Email Configuration

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${MAIL_USER:noreply@chatbotpro.in}
spring.mail.password=${MAIL_PASS:placeholder}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

---

### Server Configuration

```properties
server.port=8080                       # Port application runs on
```

---

### Environment Variables Summary

| Variable | Purpose | Required | Example |
|----------|---------|----------|---------|
| `DB_URL` | PostgreSQL JDBC URL | Yes | `jdbc:postgresql://host:5432/postgres` |
| `DB_USER` | Database username | Yes | `postgres` |
| `DB_PASS` | Database password | Yes | `password123` |
| `JWT_SECRET` | JWT signing key | Yes | Long base64 string |
| `GROQ_API_KEY` | Groq API authentication | Yes | `gsk_...` |
| `MAIL_USER` | SMTP sender email | No | `noreply@chatbotpro.in` |
| `MAIL_PASS` | SMTP sender password | No | `app_password` |
| `OLLAMA_URL` | Ollama API endpoint (if used) | No | `http://localhost:11434` |

---

## Project Structure Details

### Complete Directory Tree

```
c:\Users\priya\chatboatpro/
│
├── backend/                                   # Spring Boot Backend
│   ├── pom.xml                               # Maven dependencies & build config
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/chatbotpro/
│   │   │   │   ├── ChatBotProApplication.java  # Spring Boot entry point
│   │   │   │   ├── client/
│   │   │   │   │   └── GroqClient.java       # Groq API integration
│   │   │   │   ├── controller/                # REST API endpoints
│   │   │   │   │   ├── AdvancedWidgetController.java
│   │   │   │   │   ├── AuthController.java
│   │   │   │   │   ├── AutoFAQController.java
│   │   │   │   │   ├── DashboardController.java
│   │   │   │   │   ├── FAQController.java
│   │   │   │   │   ├── WebsiteController.java
│   │   │   │   │   ├── WidgetController.java
│   │   │   │   │   └── WidgetFeaturesController.java
│   │   │   │   ├── dto/                      # Data Transfer Objects
│   │   │   │   │   ├── auth/
│   │   │   │   │   │   ├── AuthResponse.java
│   │   │   │   │   │   ├── LoginRequest.java
│   │   │   │   │   │   └── RegisterRequest.java
│   │   │   │   │   ├── chat/
│   │   │   │   │   │   ├── ChatRequest.java
│   │   │   │   │   │   └── ChatResponse.java
│   │   │   │   │   ├── dashboard/
│   │   │   │   │   │   └── DashboardResponse.java
│   │   │   │   │   ├── faq/
│   │   │   │   │   │   ├── FAQRequest.java
│   │   │   │   │   │   └── FAQResponse.java
│   │   │   │   │   └── website/
│   │   │   │   │       ├── WebsiteRequest.java
│   │   │   │   │       └── WebsiteResponse.java
│   │   │   │   ├── entity/                   # JPA Entity Models
│   │   │   │   │   ├── ChatMessage.java
│   │   │   │   │   ├── ChatSession.java
│   │   │   │   │   ├── Coupon.java
│   │   │   │   │   ├── CSATRating.java
│   │   │   │   │   ├── FAQ.java
│   │   │   │   │   ├── FAQSuggestion.java
│   │   │   │   │   ├── KnowledgeGap.java
│   │   │   │   │   ├── LeadScore.java
│   │   │   │   │   ├── Product.java
│   │   │   │   │   ├── User.java
│   │   │   │   │   └── Website.java
│   │   │   │   ├── repository/               # Spring Data JPA Repositories
│   │   │   │   │   ├── ChatMessageRepository.java
│   │   │   │   │   ├── ChatSessionRepository.java
│   │   │   │   │   ├── CouponRepository.java
│   │   │   │   │   ├── CSATRatingRepository.java
│   │   │   │   │   ├── FAQRepository.java
│   │   │   │   │   ├── FAQSuggestionRepository.java
│   │   │   │   │   ├── KnowledgeGapRepository.java
│   │   │   │   │   ├── LeadScoreRepository.java
│   │   │   │   │   ├── ProductRepository.java
│   │   │   │   │   ├── UserRepository.java
│   │   │   │   │   └── WebsiteRepository.java
│   │   │   │   ├── security/                 # Security & Authentication
│   │   │   │   │   ├── ApplicationConfig.java
│   │   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   │   ├── JwtUtils.java
│   │   │   │   │   └── SecurityConfig.java
│   │   │   │   └── service/                  # Business Logic Services
│   │   │   │       ├── AuthService.java
│   │   │   │       ├── AutoFAQService.java
│   │   │   │       ├── ChatService.java
│   │   │   │       ├── CouponService.java
│   │   │   │       ├── CRMService.java
│   │   │   │       ├── CSATService.java
│   │   │   │       ├── DashboardService.java
│   │   │   │       ├── FAQService.java
│   │   │   │       ├── FollowUpEmailService.java
│   │   │   │       ├── ImageAnalysisService.java
│   │   │   │       ├── KnowledgeGapService.java
│   │   │   │       ├── LanguageDetectionService.java
│   │   │   │       ├── LeadScoringService.java
│   │   │   │       ├── ObjectionHandlerService.java
│   │   │   │       ├── ProductRecommendationService.java
│   │   │   │       ├── ScrapedContentService.java
│   │   │   │       ├── WebCrawlerService.java
│   │   │   │       └── WebsiteService.java
│   │   │   └── resources/
│   │   │       ├── application.properties          # Configuration
│   │   │       └── application.properties.template
│   │   └── test/
│   └── target/                               # Maven build output
│       ├── classes/
│       ├── generated-sources/
│       └── maven-status/
│
├── customersupport/                          # CRM/Support module (folder)
│
├── frontend/                                 # Frontend Web Assets
│   ├── ai_hologram_core.png                 # Branding image
│   ├── dashboard.html                       # Analytics dashboard
│   ├── heatmap.html                         # Visitor heatmap
│   ├── index.html                           # Main dashboard page
│   ├── knowledge-gaps.html                  # Training interface
│   ├── login.html                           # Login page
│   ├── register.html                        # Registration page
│   ├── templates.html                       # Email templates
│   └── widget/
│       └── widget.js                        # Embedded chat widget
│
└── widget/                                  # Widget distribution copy
    └── widget.js
```

---

### File Sizing

| Component | Lines of Code | Purpose |
|-----------|---------------|---------|
| widget.js | 895 | Embedded chat widget |
| ChatService.java | 165+ | Main chat processing |
| SecurityConfig.java | ~50 | Security configuration |
| Various DTOs | 20-50 each | Request/response models |
| Various Controllers | 40-80 each | REST endpoints |
| Entity files | 30-50 each | Database models |

---

## Dependencies

### Maven Dependencies (pom.xml)

#### Spring Boot Starters
```xml
<!-- Web MVC Framework -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Data JPA (Hibernate ORM) -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Spring Security -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- Mail Support -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

#### Database Drivers
```xml
<!-- PostgreSQL -->
<dependency>
  <groupId>org.postgresql</groupId>
  <artifactId>postgresql</artifactId>
  <scope>runtime</scope>
</dependency>

<!-- H2 (Development) -->
<dependency>
  <groupId>com.h2database</groupId>
  <artifactId>h2</artifactId>
  <scope>runtime</scope>
</dependency>
```

#### Authentication/JWT
```xml
<!-- JJWT API -->
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-api</artifactId>
  <version>0.11.5</version>
</dependency>
<!-- JJWT Impl -->
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-impl</artifactId>
  <version>0.11.5</version>
  <scope>runtime</scope>
</dependency>
<!-- JJWT Jackson -->
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-jackson</artifactId>
  <version>0.11.5</version>
  <scope>runtime</scope>
</dependency>
```

#### Web Scraping & Processing
```xml
<!-- JSoup (HTML Parser) -->
<dependency>
  <groupId>org.jsoup</groupId>
  <artifactId>jsoup</artifactId>
  <version>1.17.2</version>
</dependency>

<!-- Apache PDFBox -->
<dependency>
  <groupId>org.apache.pdfbox</groupId>
  <artifactId>pdfbox</artifactId>
  <version>3.0.3</version>
</dependency>
```

#### Utilities
```xml
<!-- Project Lombok -->
<dependency>
  <groupId>org.projectlombok</groupId>
  <artifactId>lombok</artifactId>
  <optional>true</optional>
</dependency>
```

#### Testing
```xml
<!-- Spring Boot Test -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-test</artifactId>
  <scope>test</scope>
</dependency>
```

---

### Dependency Versions Summary

| Dependency | Version | Purpose |
|-----------|---------|---------|
| Java | 17+ | Language runtime |
| Spring Boot | 3.3.4 | Framework |
| Spring Data JPA | 6.x | ORM/Data access |
| Spring Security | 3.x | Authentication |
| JJWT | 0.11.5 | JWT token creation |
| PostgreSQL Driver | Latest | Database |
| JSoup | 1.17.2 | Web scraping |
| PDFBox | 3.0.3 | PDF processing |
| Lombok | Latest | Code generation |

---

## Development Workflow

### 1. Local Development Setup

#### Prerequisites
```bash
# Install Java 17+
# Install Maven 3.6+
# Install PostgreSQL or use Supabase account
```

#### Clone & Build
```bash
cd c:\Users\priya\chatboatpro\backend
mvn clean install
mvn spring-boot:run
```

#### Backend Starts At
```
http://localhost:8080
```

---

### 2. Configuration for Development

#### Create `application-dev.properties`
```properties
# Use H2 in-memory database
spring.datasource.url=jdbc:h2:mem:chatbotdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true

# JPA/Hibernate
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=create-drop

# Development API keys
groq.api.key=sk_test_...
jwt.secret=dev-secret-key-very-long-string...
```

#### Launch
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

---

### 3. Frontend Development

#### Static File Serving
- Place HTML files in `frontend/` folder
- Access via: `http://localhost:8080/frontend/index.html`
- Or set up separate Node.js server for live reload

#### Widget Testing
```html
<!-- In any HTML file -->
<script 
  async 
  src="http://localhost:8080/frontend/widget/widget.js" 
  data-bot-id="demo-token">
</script>
```

---

### 4. Testing the API

#### Register User
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Test User",
    "email": "test@example.com",
    "password": "TestPassword123"
  }'
```

#### Response
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "test@example.com",
  "fullName": "Test User"
}
```

#### Create Website
```bash
curl -X POST http://localhost:8080/api/v1/websites \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "domain": "example.com",
    "name": "Example Corp",
    "botName": "ExampleBot",
    "welcomeMessage": "Welcome to Example!"
  }'
```

#### Send Chat Message
```bash
curl -X POST http://localhost:8080/api/v1/widget/chat \
  -H "Content-Type: application/json" \
  -d '{
    "botToken": "bot-token-from-website",
    "sessionId": "session-123",
    "message": "What are your shipping rates?"
  }'
```

---

### 5. Database Inspection

#### H2 Console (Development)
```
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:chatbotdb
Username: sa
Password: (leave blank)
```

#### PostgreSQL (Production)
```bash
psql -h db.fltmfuntfpzlkwqfljus.supabase.co \
     -U postgres \
     -d postgres
```

---

### 6. Debugging & Logging

#### Enable Debug Logging
```properties
logging.level.com.chatbotpro=DEBUG
logging.level.org.springframework.security=DEBUG
```

#### View Logs
```bash
# Maven run
mvn spring-boot:run -Dspring-boot.run.arguments="--logging.level.com.chatbotpro=DEBUG"
```

---

### 7. Common Development Tasks

#### Add New API Endpoint
1. Create DTO (in `dto/` package)
2. Add Repository method (if new query)
3. Add Service method (business logic)
4. Add Controller method (REST endpoint)
5. Test with cURL or Postman

#### Add New Entity
1. Create Entity class (with @Entity, @Table)
2. Create Repository interface (extends JpaRepository)
3. Add fields, getters/setters (use @Data from Lombok)
4. Database schema auto-updates via Hibernate

#### Modify Database Schema
- Add/remove fields in Entity class
- Set `spring.jpa.hibernate.ddl-auto=update` in production
- Or set `spring.jpa.hibernate.ddl-auto=create-drop` in development

---

### 8. Deployment

#### Build for Production
```bash
mvn clean package
# Generates: backend/target/backend-0.0.1-SNAPSHOT.jar
```

#### Deploy to Render
```bash
# 1. Create Render account
# 2. Connect GitHub repository
# 3. Set environment variables in Render dashboard
# 4. Deploy
```

#### Environment Variables on Render
```
DB_URL=jdbc:postgresql://db.fltmfuntfpzlkwqfljus.supabase.co:5432/postgres
DB_USER=postgres
DB_PASS=your-secure-password
JWT_SECRET=long-secure-random-string
GROQ_API_KEY=gsk_...
```

---

## Appendix: Quick Reference

### Key Endpoints Summary

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/v1/auth/register` | Register new user |
| POST | `/api/v1/auth/authenticate` | Login user |
| POST | `/api/v1/websites` | Create website |
| GET | `/api/v1/websites` | List websites |
| GET | `/api/v1/widget/config/{token}` | Get widget config (public) |
| POST | `/api/v1/widget/chat` | Send message (public) |
| GET | `/api/v1/faqs/website/{id}` | List FAQs |
| POST | `/api/v1/faqs` | Create FAQ |
| GET | `/api/v1/dashboard/stats` | Dashboard stats |
| GET | `/api/v1/dashboard/sessions/{id}` | Live sessions |

---

### Default Values

| Parameter | Value |
|-----------|-------|
| Server Port | 8080 |
| JWT Expiration | 24 hours |
| Trial Message Limit | 150 |
| Trial Slowdown Delay | 5 seconds |
| Web Crawl Timeout | 30 seconds |
| Coupon Timer | 300 seconds (5 min) |
| Lead Scoring: Hot Threshold | 61+ |
| Lead Scoring: Warm Threshold | 31-60 |
| Lead Scoring: Cold Threshold | 0-30 |
| Conversation Memory | 8 messages |
| Max Scraped Content | 15,000 characters |

---

### Useful Links

| Resource | URL |
|----------|-----|
| Spring Boot Docs | https://spring.io/projects/spring-boot |
| Spring Security | https://spring.io/projects/spring-security |
| Groq API | https://console.groq.com |
| Supabase | https://supabase.com |
| JSoup Documentation | https://jsoup.org |
| JWT Introduction | https://jwt.io |

---

**End of Documentation**

---

*This documentation was compiled on March 4, 2026. ChatBotPro is a comprehensive AI customer support platform enabling businesses to deploy intelligent chatbots with advanced analytics, lead scoring, and conversation intelligence capabilities.*
