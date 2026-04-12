rag project goal

"I built a RAG-based incident assistant where documents are retrieved using vector similarity and passed as context to Gemini,
which generates structured root cause analysis."


note: not using actual embedding (class enbedding service as i dont have access to embedding api)
===============================================
User asks a debugging question
↓
System searches past incidents
↓
Retrieve relevant documents
↓
Send them to LLM
↓
Generate answer

Core RAG Pipeline
User Question
↓
Embedding
↓
Vector Database
↓
Retrieve similar docs
↓
LLM (Gemini)
↓
Final Answer

=======================

Step 1: Create Embedding Model

We need a class to store:

text

vector