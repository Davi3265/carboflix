// Cliente da API REST do CarboFlix. Todas as rotas, exceto /auth, levam o token JWT.
import { sessao } from './session.js';

export class ApiError extends Error {
  constructor(status, mensagem, campos) {
    super(mensagem);
    this.status = status;
    this.campos = campos || {};
  }
}

// Chamado quando a API responde 401 (token ausente, inválido ou expirado).
let aoExpirar = () => {};
export function quandoSessaoExpirar(fn) { aoExpirar = fn; }

async function request(metodo, caminho, corpo) {
  const headers = { Accept: 'application/json' };
  if (corpo !== undefined) headers['Content-Type'] = 'application/json';
  if (sessao.token && !caminho.startsWith('/auth/')) headers.Authorization = `Bearer ${sessao.token}`;

  let resposta;
  try {
    resposta = await fetch(caminho, { method: metodo, headers, body: corpo === undefined ? undefined : JSON.stringify(corpo) });
  } catch {
    throw new ApiError(0, 'Não foi possível conectar à API. Verifique se o servidor está rodando.');
  }

  if (resposta.status === 204) return null;
  const texto = await resposta.text();
  let dados = null;
  if (texto) {
    try { dados = JSON.parse(texto); } catch { dados = null; }
  }

  if (!resposta.ok) {
    // Corpo no formato do GlobalExceptionHandler: { status, mensagem, campos?, timestamp }
    const mensagem = dados?.mensagem || `Erro ${resposta.status} ao chamar a API.`;
    if (resposta.status === 401 && !caminho.startsWith('/auth/')) aoExpirar();
    throw new ApiError(resposta.status, mensagem, dados?.campos);
  }
  return dados;
}

export const api = {
  // Auth
  login: (email, senha) => request('POST', '/auth/login', { email, senha }),
  registrar: (dados) => request('POST', '/auth/register', dados),

  // Usuário (a API só devolve a própria conta)
  minhaConta: async () => (await request('GET', '/usuarios'))[0],
  atualizarConta: (id, dados) => request('PUT', `/usuarios/${id}`, dados),
  excluirConta: (id) => request('DELETE', `/usuarios/${id}`),

  // Perfis da conta autenticada
  perfis: () => request('GET', '/perfis'),
  criarPerfil: (dados) => request('POST', '/perfis', dados),
  atualizarPerfil: (id, dados) => request('PUT', `/perfis/${id}`, dados),
  excluirPerfil: (id) => request('DELETE', `/perfis/${id}`),

  // Filmes
  filmes: () => request('GET', '/filmes'),
  filme: (id) => request('GET', `/filmes/${id}`),
  criarFilme: (dados) => request('POST', '/filmes', dados),
  atualizarFilme: (id, dados) => request('PUT', `/filmes/${id}`, dados),
  excluirFilme: (id) => request('DELETE', `/filmes/${id}`),

  // Categorias
  categorias: () => request('GET', '/categorias'),
  criarCategoria: (dados) => request('POST', '/categorias', dados),
  atualizarCategoria: (id, dados) => request('PUT', `/categorias/${id}`, dados),
  excluirCategoria: (id) => request('DELETE', `/categorias/${id}`),

  // Avaliações
  avaliacoes: () => request('GET', '/avaliacoes'),
  criarAvaliacao: (dados) => request('POST', '/avaliacoes', dados),
  atualizarAvaliacao: (id, dados) => request('PUT', `/avaliacoes/${id}`, dados),
  excluirAvaliacao: (id) => request('DELETE', `/avaliacoes/${id}`),
};
