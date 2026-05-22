# Refatoração Visual Completa — PokeDungeon (Estilo Pokémon Diamond)

Refatorar toda a identidade visual do PokeDungeon para seguir fielmente o [GAME_DESIGN.md](file:///Users/henrique/pokedungeon/GAME_DESIGN.md), criando assets **coerentes entre si** com estilo inspirado em Pokémon Diamond/Pearl/Platinum (Nintendo DS).

## Estado Atual dos Assets

| Asset | Arquivo | Tamanho | Problema |
|-------|---------|---------|----------|
| Floor tile | `tiles/floor.png` | 32x32 RGB | Sem transparência, estilo inconsistente |
| Wall tile | `tiles/wall.png` | 32x32 RGB | Sem transparência, estilo inconsistente |
| Door tile | `tiles/door.png` | 32x32 RGBA | Estilo inconsistente |
| Chest tile | `tiles/chest.png` | 32x32 RGBA | Estilo inconsistente |
| Player | `sprites/personagem2.png` | 288x288 RGBA | Sprite grande demais, não é 32x32, apenas 1 frame por direção |
| Charmander front | `sprites/charmander.PNG` | 64x64 RGBA | Escala/estilo diferente dos outros |
| Charmander back | `sprites/charmandercostas.PNG` | 32x32 RGBA | Escala diferente do front |
| Squirtle front | `sprites/squirtle.PNG` | 64x64 RGBA | OK mas inconsistente |
| Squirtle back | `sprites/squirtledecostas.PNG` | 32x32 RGBA | Escala diferente |
| Rattata | `sprites/rattata.PNG` | 64x64 RGBA | Sem back sprite |
| Zubat | `sprites/zubat.PNG` | 64x64 RGBA | Sem back sprite |
| Onix | `sprites/onyx.PNG` | 64x64 RGBA | Sem back sprite |
| Dialog box | `ui/barratexto.PNG` | 426x80 RGBA | Gerada programaticamente, OK |

> [!IMPORTANT]
> **Problema principal**: Os assets visuais não compartilham a mesma paleta, densidade de pixels, outline ou estilo artístico. Parecem vir de fontes diferentes.

---

## Paleta de Cores Global

Antes de gerar qualquer asset, definimos a paleta mestre inspirada em Pokémon Diamond:

| Uso | Cor | Hex |
|-----|-----|-----|
| Dungeon floor (claro) | Cinza-bege quente | `#C8B888` |
| Dungeon floor (escuro) | Marrom-cinza | `#A89868` |
| Dungeon wall | Cinza-pedra escuro | `#686868` |
| Dungeon wall highlight | Cinza médio | `#989898` |
| Grass | Verde suave | `#68A848` |
| Grass dark | Verde floresta | `#488830` |
| Water | Azul suave | `#5890C8` |
| Water dark | Azul profundo | `#3870A8` |
| UI background | Branco-creme | `#F0F0E8` |
| UI border | Cinza-escuro | `#484848` |
| UI border highlight | Cinza-azulado | `#687888` |
| Text | Preto | `#282828` |
| HP green | Verde vivo | `#48C848` |
| HP yellow | Amarelo | `#E8C830` |
| HP red | Vermelho | `#E84848` |
| Player jacket | Vermelho DS | `#D03030` |
| Player hat | Branco | `#F0F0F0` |
| Outline | Preto macio | `#303030` |

Todos os assets abaixo serão gerados usando **estritamente esta paleta**.

---

## Proposed Changes

### Fase 1 — Tileset Principal (Dungeon)

Gerar todos os tiles com **estilo consistente**, mesmo outline, mesma densidade de pixels.

#### [NEW] `assets/tiles/floor.png` (substitui o atual)
- 32x32 RGBA
- Chão de dungeon/caverna estilo Pokémon Diamond
- Tons de `#C8B888` / `#A89868`
- Padrão sutil de pedra/terra

#### [NEW] `assets/tiles/wall.png` (substitui o atual)
- 32x32 RGBA
- Parede de pedra estilo caverna Pokémon Diamond
- Tons de `#686868` / `#989898`
- Outline `#303030`

#### [NEW] `assets/tiles/door.png` (substitui o atual)
- 32x32 RGBA
- Abertura/portal estilo dungeon Pokémon Diamond
- Transparência correta

#### [NEW] `assets/tiles/chest.png` (substitui o atual)
- 32x32 RGBA
- Baú de tesouro pixel art

#### [NEW] `assets/tiles/grass.png`
- 32x32 RGBA
- Grama estilo rotas de Pokémon Diamond
- Tons de `#68A848` / `#488830`

#### [NEW] `assets/tiles/water.png`
- 32x32 RGBA
- Água estilo Pokémon Diamond
- Tons de `#5890C8` / `#3870A8`

---

### Fase 2 — Player Sprite

#### [NEW] `assets/sprites/player/player_down.png`
- 32x32 RGBA
- Protagonista olhando para baixo (idle)

#### [NEW] `assets/sprites/player/player_up.png`
- 32x32 RGBA
- Protagonista olhando para cima (idle)

#### [NEW] `assets/sprites/player/player_left.png`
- 32x32 RGBA
- Protagonista olhando para esquerda (idle)

#### [NEW] `assets/sprites/player/player_right.png`
- 32x32 RGBA
- Protagonista olhando para direita (idle)

Todas as 4 direções devem ser **pixel-perfect 32x32**, com outline `#303030`, roupa estilo protagonista de Pokémon Diamond (boné branco, jaqueta vermelha), desenhados com a **mesma paleta mestre**.

---

### Fase 3 — UI / HUD (Estilo Pokémon Diamond)

#### [NEW] `assets/ui/dialog_box.png` (substitui barratexto.PNG)
- 426x80 RGBA
- Caixa de diálogo estilo Pokémon Diamond
- Fundo `#F0F0E8`, borda `#484848`
- Cantos retos, padding interno, visual limpo

#### [NEW] `assets/ui/hp_bar_player.png`
- 140x44 RGBA
- Caixa de status do jogador estilo Pokémon Diamond
- Nome, nível, barra HP

#### [NEW] `assets/ui/hp_bar_enemy.png`
- 140x36 RGBA
- Caixa de status do inimigo

#### [NEW] `assets/ui/battle_menu.png`
- ~130x80 RGBA
- Menu de ações com layout grid 2x2 (ATTACK, ITEM, RUN, ?)
- Fundo claro, borda escura

---

### Fase 4 — Pokémon Sprites

Todos os Pokémon sprites serão gerados com **mesmo estilo, mesmo outline, mesma paleta base**.

#### [NEW] `assets/sprites/pokemon/charmander_front.png`
- 64x64 RGBA — estilo Nintendo DS pixel art

#### [NEW] `assets/sprites/pokemon/charmander_back.png`
- 64x64 RGBA (padronizando; o atual back é 32x32)

#### [NEW] `assets/sprites/pokemon/squirtle_front.png`
- 64x64 RGBA

#### [NEW] `assets/sprites/pokemon/squirtle_back.png`
- 64x64 RGBA

#### [NEW] `assets/sprites/pokemon/rattata_front.png`
- 64x64 RGBA

#### [NEW] `assets/sprites/pokemon/zubat_front.png`
- 64x64 RGBA

#### [NEW] `assets/sprites/pokemon/onix_front.png`
- 64x64 RGBA

---

### Fase 5 — Código: Atualizar caminhos de assets

#### [MODIFY] [DungeonScreen.java](file:///Users/henrique/pokedungeon/core/src/main/java/com/pokedungeon/game/screens/DungeonScreen.java)
- Trocar carregamento do spritesheet por 4 sprites individuais (`player/player_down.png`, etc.)
- Trocar `personagem2.png` pelo novo sistema de sprites por direção
- Adicionar Nearest filter em todos os tiles

#### [MODIFY] [BattleScreen.java](file:///Users/henrique/pokedungeon/core/src/main/java/com/pokedungeon/game/screens/BattleScreen.java)
- Atualizar `frontSpriteMap` e `backSpriteMap` com novos caminhos (`sprites/pokemon/charmander_front.png`, etc.)
- Trocar `ui/barratexto.PNG` por `ui/dialog_box.png`
- Usar novas texturas de UI para HP bars e menu de combate
- Padronizar back sprites para 64x64

#### [MODIFY] [PartyMenuScreen.java](file:///Users/henrique/pokedungeon/core/src/main/java/com/pokedungeon/game/screens/PartyMenuScreen.java)
- Trocar referência a `ui/barratexto.PNG` por `ui/dialog_box.png`

---

### Fase 6 — Tela de Batalha: Layout estilo Pokémon Diamond

#### [MODIFY] [BattleScreen.java](file:///Users/henrique/pokedungeon/core/src/main/java/com/pokedungeon/game/screens/BattleScreen.java)
- Redesenhar layout para separar: **caixa de texto** (inferior esquerda) + **menu de ações** (inferior direita)
- Posicionar HP bar do inimigo no topo esquerdo
- Posicionar HP bar do jogador no centro-direita, abaixo do sprite do jogador
- Aplicar cores da paleta mestre na renderização

---

### Fase 7 — Limpeza e Ajustes Finais

#### [DELETE] Assets antigos que serão substituídos:
- `sprites/personagem.PNG`
- `sprites/personagem2.png`
- `sprites/charmander.PNG`, `sprites/charmandercostas.PNG`
- `sprites/squirtle.PNG`, `sprites/squirtledecostas.PNG`
- `sprites/rattata.PNG`, `sprites/zubat.PNG`, `sprites/onyx.PNG`
- `ui/barratexto.PNG`

---

## User Review Required

> [!IMPORTANT]
> **Geração de imagens via IA**: Os assets serão gerados com a ferramenta `generate_image`. Imagens geradas por IA nunca são pixel-perfect em 32x32 nativo. Minha abordagem será:
> 1. Gerar a imagem com prompt detalhado e consistente
> 2. Processar com Python/Pillow para redimensionar para tamanho exato e aplicar nearest-neighbor (sem anti-aliasing)
> 3. Garantir transparência correta
> 
> O resultado será **bom e consistente**, mas não será idêntico a pixel art feita à mão. Se você tiver sprites prontos de melhor qualidade para substituir depois, os caminhos no código já estarão corretos.

> [!WARNING]
> **Volume de trabalho**: São ~20 imagens para gerar + ~3 arquivos Java para modificar. Isso vai demorar um pouco, mas garante que tudo fique coerente no final.

## Open Questions

1. **Pokémon back sprites**: Atualmente só Charmander e Squirtle têm sprites traseiros. Devo gerar back sprites para Rattata, Zubat e Onix também (para caso o jogador os capture no futuro), ou apenas para os 2 que o jogador já tem?

2. **Grass e Water tiles**: Você quer que eu já integre esses tiles no mapa (adicionando variação de terreno nas salas), ou apenas crie os assets para uso futuro?

3. **Menu Screen**: O [MenuScreen.java](file:///Users/henrique/pokedungeon/core/src/main/java/com/pokedungeon/game/screens/MenuScreen.java) também precisa de refatoração visual, ou foco apenas na Dungeon e Batalha?

---

## Verification Plan

### Automated Tests
- `./gradlew build` — compilação sem erros
- Verificar que todos os caminhos de assets existem e são carregáveis

### Manual Verification
- Rodar o jogo (`./gradlew lwjgl3:run`) e verificar:
  - Tiles do mapa: sem cortes, alinhamento perfeito
  - Player sprite: 4 direções funcionando, escala correta
  - Tela de batalha: layout Pokémon Diamond, HP bars, dialog box
  - Todos os sprites visualmente coerentes entre si
