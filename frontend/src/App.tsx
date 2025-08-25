import React, {useEffect, useState, useRef} from 'react'

type Msg ={
    who: 'user' | 'bot',
    message: string,
    agent?:string,
    executionTimeMs?:number,
    timestamp: number,
    userId?: string
}


const apiBase = (import.meta as any).env.VITE_API_BASE_URL || 'http://localhost:8080'


export default function App() {
  const [conversationId, setConversationId] = useState('conv-' + Date.now())
  const [idUsuario, setIdUsuario] = useState(localStorage.getItem('idUsuario') || 'cliente789')
  const [mensagem, setMensagem] = useState('')
  const [history, setHistory] = useState<Msg[]>([])
  const [loading, setLoading] = useState(false)

  const messagesEndRef = useRef<HTMLDivElement>(null);
  


  useEffect(() => {
    localStorage.setItem('idUsuario', idUsuario)
  }, [idUsuario])

  const fetchHistory = async () => {
  try{
    
    const res = await 
    fetch(`${apiBase}/chat/history/${conversationId}`);
    if (res.ok) {
      const data = await res.json() as Msg[];
      setHistory(prev =>{
        const lastTimestamp = prev.length ? prev[prev.length -1].timestamp: 0;
        return data.filter(msg => msg.timestamp > lastTimestamp);
      });
    }
  } catch (e){
    console.error('Erro ao bscar histórico:', e);
    }
  }

  useEffect(() => {
    fetchHistory().catch(console.error)
  }, [conversationId])

  useEffect(() =>{
  messagesEndRef.current?.scrollIntoView({ behavior: 'smooth'});
  }, [history]);

  const send = async () => {
    if (!mensagem.trim()) return
    setLoading(true)
    try {
    
      const res = await fetch(`${apiBase}/chat`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ mensagem, idUsuario, conversationId })
      })
      if (!res.ok) {
        const text = await res.text()
        alert('Erro: ' + text)
      }

      const data = await res.json();
      setHistory(prev => [...prev,
            { who: 'user', message: mensagem, timestamp: Date.now(), userId: idUsuario },
            { who: 'bot', message: data.resposta, agent: data.fontAgentResposta, timestamp: Date.now() }
      ]);

      
      setMensagem('')
    } catch (e:any) {
      alert('Falha ao enviar: ' + e?.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div style={{maxWidth: 900, margin: '0 auto', padding: 16, fontFamily: 'Inter, system-ui, Arial',
      background: 'linear-gradient(120deg, #f0f4ff, #ffffff)',
      minHeight: '100vh'}}>

      <h1 style={{ textAlign: 'center', color: '#4f8cff'}}>CHATBOT</h1>
      
      <div style={{display: 'grid', gap: 8, gridTemplateColumns: '1fr 1fr', marginBottom: 12}}>
            <label>Id_Conversa
              <input value={conversationId} onChange={e=>setConversationId(e.target.value)} style={{width:'100%', padding: 6, borderRadius: 6, border: '1px solid #ccc'}}/>
           </label>
            <label>Usuário
              <input value={idUsuario} onChange={e=>setIdUsuario(e.target.value)} style={{width:'100%', padding: 6, borderRadius: 6, border: '1px solid #ccc'}}/>
            </label>
      </div>

          


      <div style={{border:'1px solid #ddd', borderRadius:12, padding:12, marginBottom:12, height: 380, overflow:'auto', backgroundColor: '#fafafa'}}>
        {history.map((m,i)=>(

          <div key={i} style={{margin:'8px 0', display:'flex', flexDirection: m.who==='user'?'row-reverse':'row', gap:8}}>
            <div style={{minWidth:64, textAlign:'center', fontSize: 15, color: '#555'}}>
              {m.who}
            </div>
              <div style={{background: m.who === 'user' ? '#4f8cff' : '#e5e5ea', color: m.who === 'user' ? '#fff' : '#000', padding:12, borderRadius:12, flex:1, maxWidth: '70%', wordBreak: 'break-word',}}>
              
                  <div>{m.message}</div>
                  {m.agent && (
                    <small style={{ opacity: 0.7 }}>
                                     agente: {m.agent} {m.executionTimeMs ? `(${m.executionTimeMs}ms)` : ''}
                    </small>
                  )}
                      <div style={{ fontSize: 15, color: '#999', marginTop: 4 }}>
                        {new Date(m.timestamp).toLocaleTimeString()}
                      </div>
              </div>
            </div>
        ))}
    <div ref={messagesEndRef}></div>
    </div>

      <div style={{display:'flex', gap:8}}>
        <input placeholder="Escreva sua mensagem..." value={mensagem}
               onChange={e=>setMensagem(e.target.value)} style={{flex:1, padding: 20, borderRadius: 8, border: '1px solid #ccc'}}/>

        <button onClick={send} disabled={loading} style={{
        padding: '10px 16px',
        borderRadius: 8,
        border: 'none',
        backgroundColor: '#4f8cff',
        color: '#fff',
        cursor:'pointer'}}>{loading?'Enviando...':'Enviar'}</button>

        <button onClick={fetchHistory} style={{
        padding: '10px 16px',
        borderRadius: 8,
        border: '1px solid #4f8cff',
        backgroundColor: '#fff',
        color: '#4f8cff',
        cursor:'pointer'}}>Atualizar Histórico</button>
      </div>

      
       
      </div>
        

      
   
  )
}
