# 🎨 PokeDungeon - DESIGN.md

## Visão Geral

O visual do PokeDungeon deve ser fortemente inspirado em:

- Pokémon Diamond
- Pokémon Pearl
- Pokémon Platinum

Objetivo:
Criar um visual retrô moderno utilizando pixel art simples, porém organizado e consistente.

O jogo deve parecer um protótipo funcional de um RPG Pokémon clássico de Nintendo DS.

---

# 🎮 Direção Artística

## Estilo Visual

- Pixel art retrô
- Top-down 2D
- Interface inspirada em Pokémon Diamond
- Poucos efeitos visuais
- Paleta de cores suave
- Visual limpo e legível

---

# 📐 Resolução e Escala

## Resolução Base

```text
1280x720
```

## Escala Pixel Art

* Upscale 3x ou 4x
* Pixel perfect

## Tile Size

```text
32x32
```

---

# 🗺️ MAPA (Dungeon / Overworld)

## Inspiração

O mapa deve lembrar:

* rotas de Pokémon Diamond
* cavernas simples
* pequenas dungeons

---

## Estrutura do Mapa

O mapa deve possuir:

* paredes
* chão
* grama
* portas
* água (opcional)
* pedras
* caminhos conectados

---

## Estilo do Tileset

### Cores

* tons de verde
* marrom escuro
* cinza pedra
* azul suave

### Evitar

* neon
* brilho excessivo
* efeitos modernos
* texturas ultra detalhadas

---

## Navegação

Movimentação:

* grid-based
* 4 direções
* animação simples

---

# 👤 PERSONAGENS

## Player

Inspirado em:

* protagonista de Pokémon Diamond

Características:

* sprite pequeno
* 32x32
* roupa simples
* outline escura
* poucas animações

---

## Pokémon

### Estilo

* sprites frontais estilo Nintendo DS
* pixel art simples
* visual carismático

### Tamanho

* pequeno/médio
* poucos detalhes

### Animações

* idle simples
* shake leve ao atacar

---

# ⚔️ TELA DE BATALHA

## Inspiração

A batalha deve ser MUITO inspirada em Pokémon Diamond.

---

# Layout da Batalha

## Parte Superior

Pokémon inimigo.

Elementos:

* sprite inimigo
* barra de HP
* nome
* nível

---

## Parte Inferior

Pokémon do jogador.

Elementos:

* sprite traseiro
* HP
* nome
* nível

---

# 🟩 BARRA DE STATUS (HP BAR)

## Estilo

Inspirada diretamente em Pokémon Diamond.

---

## Estrutura

### Conteúdo

* nome do pokémon
* nível
* barra de HP
* valor atual de HP

---

## Cores da Vida

| Estado   | Cor      |
| -------- | -------- |
| HP Alto  | Verde    |
| HP Médio | Amarelo  |
| HP Baixo | Vermelho |

---

## Formato

* borda escura
* fundo claro
* cantos retos
* visual simples

---

# 💬 CAIXAS DE TEXTO

## Inspiração

Pokémon Diamond / Pearl.

---

## Estrutura Visual

### Caixa Principal

* fundo branco/claro
* borda preta escura
* padding interno
* texto pixelado

---

## Características

* ocupa parte inferior da tela
* largura grande
* altura pequena/média
* visual limpo

---

## Fonte

Fonte estilo pixel.

Sugestões:

* Press Start 2P
* Pixel Operator
* VT323

---

## Comportamento

A caixa de texto deve:

* aparecer gradualmente
* mostrar ações da batalha
* mostrar diálogos simples

Exemplos:

```text
Pikachu used Thunder Shock!
Charmander took 12 damage!
```

---

# 🎯 MENU DE COMBATE

## Layout

Inspirado em Pokémon Diamond.

---

## Estrutura

Fica no canto inferior direito.

Opções:

* ATTACK
* ITEM
* RUN

---

## Visual

* botões quadrados
* bordas simples
* fundo claro
* destaque na opção selecionada

---

## Navegação

Controle:

* teclado
* setas/WASD
* ENTER para confirmar

---

# 🎒 INVENTÁRIO

## Visual

Menu simples inspirado em Pokémon clássico.

---

## Estrutura

Lista vertical:

* nome do item
* quantidade
* descrição simples

---

# 🎨 PALETA DE CORES

## Base

Inspirada em Pokémon Diamond.

### Tons principais

* azul suave
* verde floresta
* marrom terra
* cinza pedra
* branco interface

---

## Evitar

* cores neon
* gradientes modernos
* transparências exageradas
* efeitos futuristas

---

# ✨ EFEITOS VISUAIS

## Permitidos

* fade simples
* shake leve
* transição curta
* blink em dano

---

## NÃO usar

* partículas complexas
* bloom
* motion blur
* shaders avançados

---

# 🎵 ÁUDIO

## Música

Opcional.

Estilo:

* retrô
* Nintendo DS
* loop simples

---

## Efeitos Sonoros

* seleção de menu
* ataque
* dano
* vitória

---

# 🖼️ HUD

## Dungeon

Mostrar:

* HP atual
* pokémon ativo
* itens
* sala atual

---

## Batalha

Mostrar:

* HP bars
* mensagens
* menu de combate

---

# 📁 Estrutura de Assets

```text
assets/
├── sprites/
│   ├── player/
│   ├── pokemon/
│   └── enemies/
├── tiles/
├── ui/
├── fonts/
├── audio/
└── maps/
```

---

# 📌 Objetivo Final

O jogo deve parecer:

* um RPG Pokémon clássico
* simples
* organizado
* nostálgico
* consistente visualmente

O foco NÃO é realismo.

O foco é:

* clareza
* estilo retrô
* simplicidade
* identidade visual consistente
