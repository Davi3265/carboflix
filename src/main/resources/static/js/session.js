// Guarda no navegador o token JWT, os dados da conta e o perfil escolhido.
const KEY = 'carboflix.sessao';

function ler() {
  try {
    return JSON.parse(localStorage.getItem(KEY)) || {};
  } catch {
    return {};
  }
}

function gravar(dados) {
  try {
    localStorage.setItem(KEY, JSON.stringify(dados));
  } catch {
    // Navegação privada sem armazenamento: a sessão vale só enquanto a aba estiver aberta.
  }
  memoria = dados;
}

let memoria = ler();

export const sessao = {
  get token() { return memoria.token || null; },
  get usuario() { return memoria.usuario || null; },
  get perfil() { return memoria.perfil || null; },

  entrar(token) { gravar({ token }); },
  definirUsuario(usuario) { gravar({ ...memoria, usuario }); },
  escolherPerfil(perfil) { gravar({ ...memoria, perfil }); },
  esquecerPerfil() { const { perfil, ...resto } = memoria; gravar(resto); },
  sair() { gravar({}); },
};
