// Roteador da SPA (rotas com #) e barra de navegação das telas autenticadas.
import { api, quandoSessaoExpirar } from './api.js';
import { sessao } from './session.js';
import { $, avatar, carregando, esc, icone, logo, toast } from './ui.js';
import * as login from './views/login.js';
import * as perfis from './views/perfis.js';
import * as catalogo from './views/catalogo.js';
import * as filme from './views/filme.js';
import * as gerenciarFilmes from './views/gerenciar-filmes.js';
import * as categorias from './views/categorias.js';
import * as conta from './views/conta.js';

const app = $('#app');

// publica: não exige login · semNav: tela cheia, sem a barra superior
const ROTAS = [
  { padrao: /^#\/login$/, view: login, publica: true, semNav: true },
  { padrao: /^#\/perfis$/, view: perfis, semNav: true },
  { padrao: /^#\/catalogo$/, view: catalogo, menu: 'catalogo' },
  { padrao: /^#\/filmes\/(\d+)$/, view: filme, menu: 'catalogo' },
  { padrao: /^#\/gerenciar$/, view: gerenciarFilmes, menu: 'gerenciar' },
  { padrao: /^#\/categorias$/, view: categorias, menu: 'categorias' },
  { padrao: /^#\/conta$/, view: conta, menu: 'conta' },
];

const MENU = [['catalogo', 'Catálogo'], ['categorias', 'Categorias'], ['gerenciar', 'Gerenciar filmes'], ['conta', 'Minha conta']];

export function navegar(hash) {
  if (location.hash === hash) renderizar();
  else location.hash = hash;
}

export function sair(mensagem) {
  sessao.sair();
  if (mensagem) toast(mensagem, 'err');
  navegar('#/login');
}

quandoSessaoExpirar(() => sair('Sua sessão expirou. Entre novamente.'));

function barraNavegacao(menuAtivo) {
  const perfil = sessao.perfil;
  const nome = perfil?.nomePerfil || sessao.usuario?.nome || 'Escolher perfil';
  return `
    <header class="nav">
      ${logo()}
      <nav class="nav-links">
        ${MENU.map(([id, rotulo]) => `<a href="#/${id}" class="${id === menuAtivo ? 'active' : ''}">${rotulo}</a>`).join('')}
      </nav>
      <div class="nav-profile">
        <a href="#/perfis" title="Trocar de perfil">${avatar(nome, perfil?.avatarUrl, perfil?.id)}<span>${esc(nome)}</span></a>
        <button class="nav-logout" data-sair title="Sair" aria-label="Sair">${icone.sair}</button>
      </div>
    </header>`;
}

let renderAtual = 0;
async function renderizar() {
  const hash = location.hash || '#/';
  const rota = ROTAS.find((r) => r.padrao.test(hash));

  if (!rota) {
    location.replace(sessao.token ? (sessao.perfil ? '#/catalogo' : '#/perfis') : '#/login');
    return;
  }
  if (!rota.publica && !sessao.token) { location.replace('#/login'); return; }
  if (rota.publica && sessao.token) { location.replace('#/perfis'); return; }

  const id = ++renderAtual;
  document.title = 'CarboFlix';
  window.scrollTo(0, 0);

  try {
    if (!rota.publica && !sessao.usuario) sessao.definirUsuario(await api.minhaConta());
  } catch {
    return; // 401 já redirecionou para o login
  }
  if (id !== renderAtual) return;

  app.innerHTML = rota.semNav ? '' : barraNavegacao(rota.menu);
  if (!rota.semNav) $('[data-sair]', app).addEventListener('click', () => sair());

  const conteudo = document.createElement('main');
  conteudo.innerHTML = carregando();
  app.append(conteudo);

  const params = hash.match(rota.padrao).slice(1);
  try {
    await rota.view.render(conteudo, { params, navegar, recarregar: renderizar, sair, ehAtual: () => id === renderAtual });
  } catch (erro) {
    if (id !== renderAtual || erro?.status === 401) return;
    conteudo.innerHTML = `<div class="page"><div class="empty"><strong>Não foi possível carregar esta tela.</strong>${esc(erro.message)}</div></div>`;
  }
}

window.addEventListener('hashchange', renderizar);
renderizar();
