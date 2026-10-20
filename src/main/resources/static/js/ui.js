// Peças de interface reutilizadas pelas telas (equivalentes aos componentes do Figma).
import { ApiError } from './api.js';

// Escapa qualquer texto vindo da API antes de colocá-lo no HTML.
export function esc(valor) {
  return String(valor ?? '').replace(/[&<>"']/g, (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
}

export const $ = (seletor, raiz = document) => raiz.querySelector(seletor);
export const $$ = (seletor, raiz = document) => [...raiz.querySelectorAll(seletor)];

const svg = (corpo, extra = '') => `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true" ${extra}>${corpo}</svg>`;
export const icone = {
  play: '<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M9 6.5v11l9-5.5-9-5.5z" fill="#0E0F12"/></svg>',
  estrela: '<svg viewBox="0 0 24 24" aria-hidden="true"><path d="M12 2.5l2.94 5.96 6.58.96-4.76 4.64 1.12 6.55L12 17.52l-5.88 3.09 1.12-6.55L2.48 9.42l6.58-.96L12 2.5z" fill="currentColor"/></svg>',
  mais: svg('<path d="M12 5v14M5 12h14"/>'),
  editar: svg('<path d="M12 20h9M16.5 3.5a2.12 2.12 0 0 1 3 3L7 19l-4 1 1-4 12.5-12.5z"/>'),
  lixeira: svg('<path d="M3 6h18M8 6V4h8v2M19 6l-1 14H6L5 6"/>'),
  busca: svg('<circle cx="11" cy="11" r="7"/><path d="m20 20-3.5-3.5"/>'),
  voltar: svg('<path d="m15 18-6-6 6-6"/>'),
  sair: svg('<path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4M16 17l5-5-5-5M21 12H9"/>'),
  alerta: svg('<circle cx="12" cy="12" r="9"/><path d="M12 8v5M12 16.5v.01"/>'),
  ok: svg('<circle cx="12" cy="12" r="9"/><path d="m8 12 3 3 5-6"/>'),
};

export const logo = () => `<a class="logo" href="#/catalogo"><span class="logo-mark">${icone.play}</span><span>Carbo<b>Flix</b></span></a>`;

// Mesmas combinações de gradiente usadas nos pôsteres e avatares do protótipo.
const GRADIENTES_POSTER = [['#3B1D60', '#120A1F'], ['#0F4C5C', '#081A20'], ['#7A1F1F', '#1A0A0A'], ['#1F3B7A', '#0A1020'], ['#6B4A12', '#1A1206'], ['#145A32', '#06180D'], ['#5C0F4C', '#1A0614'], ['#2D2F7A', '#0B0C20']];
const GRADIENTES_AVATAR = [['#FF5A1F', '#7A1F5C'], ['#2F6BFF', '#0F4C5C'], ['#22C55E', '#0F4C5C'], ['#FFB020', '#7A1F1F'], ['#A855F7', '#1F3B7A']];
const indice = (chave, total) => {
  const texto = String(chave ?? '');
  let h = 0;
  for (const c of texto) h = (h * 31 + c.charCodeAt(0)) >>> 0;
  return h % total;
};
const gradiente = ([a, b]) => `background: linear-gradient(135deg, ${a}, ${b});`;
const urlSegura = (url) => (/^https?:\/\//i.test(url || '') ? url : null);

export function poster(filme, extraClasse = '') {
  const url = urlSegura(filme.posterUrl);
  const estilo = gradiente(GRADIENTES_POSTER[indice(filme.id ?? filme.titulo, GRADIENTES_POSTER.length)]);
  const img = url ? `<img src="${esc(url)}" alt="" loading="lazy" onerror="this.parentElement.classList.remove('has-img'); this.remove();">` : '';
  return `<div class="poster ${url ? 'has-img' : ''} ${extraClasse}" style="${estilo}">${img}<span class="poster-title">${esc(filme.titulo)}</span></div>`;
}

export function avatar(nome, url, chave, classe = '') {
  const seguro = urlSegura(url);
  const estilo = gradiente(GRADIENTES_AVATAR[indice(chave ?? nome, GRADIENTES_AVATAR.length)]);
  const inicial = esc((nome || '?').trim().charAt(0).toUpperCase());
  const img = seguro ? `<img src="${esc(seguro)}" alt="" onerror="this.parentElement.textContent = this.parentElement.dataset.inicial">` : '';
  return `<span class="avatar ${classe}" style="${estilo}" data-inicial="${inicial}">${img || inicial}</span>`;
}

export function estrelas(nota, grande = false) {
  const n = Math.round(nota || 0);
  return `<span class="stars ${grande ? 'lg' : ''}" aria-label="${n} de 5 estrelas">${[1, 2, 3, 4, 5].map((i) => `<span class="${i <= n ? 'on' : ''}">${icone.estrela}</span>`).join('')}</span>`;
}

export const formatarMedia = (media) => (media == null ? '–' : media.toFixed(1).replace('.', ','));
export const formatarData = (iso) => (iso ? new Date(iso).toLocaleDateString('pt-BR') : '');
export const plural = (n, um, varios) => `${n} ${n === 1 ? um : varios}`;
export const textoClassificacao = (c) => (c === 'L' ? 'Livre' : /^\d+$/.test(c || '') ? `${c} anos` : c);
export const metaFilme = (f) => `${f.anoLancamento} · ${f.duracaoMin} min · ${esc(f.classificacaoIndicativa)}`;

export const carregando = () => '<div class="loading"><div class="spinner" role="status" aria-label="Carregando"></div></div>';

// ---------- Toast ----------
export function toast(mensagem, tipo = 'ok') {
  let area = $('.toasts');
  if (!area) {
    area = document.createElement('div');
    area.className = 'toasts';
    area.setAttribute('aria-live', 'polite');
    document.body.append(area);
  }
  const el = document.createElement('div');
  el.className = `toast ${tipo}`;
  el.innerHTML = `${tipo === 'ok' ? icone.ok : icone.alerta}<span>${esc(mensagem)}</span>`;
  area.append(el);
  setTimeout(() => el.remove(), 4000);
}

// ---------- Modal ----------
export function abrirModal(conteudoHtml, { largo = false } = {}) {
  const fundo = document.createElement('div');
  fundo.className = 'modal-backdrop';
  fundo.innerHTML = `<div class="modal surface ${largo ? 'wide' : ''}" role="dialog" aria-modal="true">${conteudoHtml}</div>`;
  const fechar = () => { fundo.remove(); document.removeEventListener('keydown', aoTeclar); };
  const aoTeclar = (e) => { if (e.key === 'Escape') fechar(); };
  fundo.addEventListener('mousedown', (e) => { if (e.target === fundo) fechar(); });
  document.addEventListener('keydown', aoTeclar);
  document.body.append(fundo);
  const modal = $('.modal', fundo);
  $$('[data-fechar]', modal).forEach((b) => b.addEventListener('click', fechar));
  setTimeout(() => $('input, textarea, select', modal)?.focus(), 30);
  return { el: modal, fechar };
}

// Pede confirmação e executa `acao`. Se a API recusar (ex.: 409 por vínculo),
// a mensagem aparece no próprio diálogo. Resolve true se a ação deu certo.
export function confirmar({ titulo, mensagem, confirmar: rotulo = 'Excluir', acao }) {
  return new Promise((resolve) => {
    const { el, fechar } = abrirModal(`
      <h2>${esc(titulo)}</h2>
      <p class="muted" style="margin:0;line-height:1.55">${esc(mensagem)}</p>
      <div class="alert" hidden data-erro></div>
      <div class="modal-actions">
        <button class="btn btn-ghost" data-fechar>Cancelar</button>
        <button class="btn btn-danger" data-ok>${esc(rotulo)}</button>
      </div>`);
    const botao = $('[data-ok]', el);
    botao.addEventListener('click', () => comCarregamento(botao, async () => {
      try {
        await acao();
        fechar();
        resolve(true);
      } catch (erro) {
        mostrarErro(el, erro);
      }
    }));
    $$('[data-fechar]', el).forEach((b) => b.addEventListener('click', () => resolve(false)));
  });
}

// ---------- Formulários ----------
export function campo({ nome, rotulo, tipo = 'text', valor = '', placeholder = '', obrigatorio = false, extra = '' }) {
  const id = `f-${nome}-${Math.random().toString(36).slice(2, 7)}`;
  const entrada = tipo === 'textarea'
    ? `<textarea class="input" id="${id}" name="${nome}" placeholder="${esc(placeholder)}" ${extra}>${esc(valor)}</textarea>`
    : `<input class="input" id="${id}" name="${nome}" type="${tipo}" value="${esc(valor)}" placeholder="${esc(placeholder)}" ${obrigatorio ? 'required' : ''} ${extra}>`;
  return `<div class="field" data-campo="${nome}"><label for="${id}">${esc(rotulo)}${obrigatorio ? ' *' : ''}</label>${entrada}<span class="field-error">${icone.alerta}<span></span></span></div>`;
}

export function limparErros(raiz) {
  $$('.field.has-error', raiz).forEach((f) => f.classList.remove('has-error'));
  const alerta = $('[data-erro]', raiz);
  if (alerta) { alerta.hidden = true; alerta.innerHTML = ''; }
}

// Mostra o erro da API: mensagens por campo (400 de validação) ou um alerta geral (404/409/...).
export function mostrarErro(raiz, erro) {
  limparErros(raiz);
  const campos = erro instanceof ApiError ? erro.campos : {};
  let algumCampo = false;
  for (const [nome, msg] of Object.entries(campos)) {
    const f = $(`[data-campo="${nome}"]`, raiz);
    if (f) { f.classList.add('has-error'); $('.field-error span', f).textContent = msg; algumCampo = true; }
  }
  const alerta = $('[data-erro]', raiz);
  if (!algumCampo || !alerta) {
    const mensagem = erro?.message || 'Algo deu errado. Tente novamente.';
    if (alerta) { alerta.innerHTML = `${icone.alerta}<span>${esc(mensagem)}</span>`; alerta.hidden = false; } else toast(mensagem, 'err');
  }
}

export async function comCarregamento(botao, acao) {
  const texto = botao.textContent;
  botao.disabled = true;
  botao.textContent = 'Aguarde…';
  try { return await acao(); } finally { botao.disabled = false; botao.textContent = texto; }
}

export const valorOuNulo = (v) => { const t = (v ?? '').toString().trim(); return t === '' ? null : t; };
export const numeroOuNulo = (v) => { const t = (v ?? '').toString().trim(); return t === '' ? null : Number(t); };
