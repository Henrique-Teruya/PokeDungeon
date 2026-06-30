# GAMEPLAY_LOOP.md

# 🎮 PokeDungeon - Gameplay Loop

## Objetivo

O objetivo do PokeDungeon é criar um gameplay extremamente divertido, estratégico e altamente rejogável.

O jogador deve sentir que **sempre existe mais uma run para fazer**, mais um Pokémon para capturar e mais um andar para superar.

O jogo deve recompensar constantemente o jogador, sem ser injusto ou excessivamente dependente de sorte.

---

# Filosofia

O jogador deve experimentar constantemente os seguintes sentimentos:

- Progressão
- Descoberta
- Escolhas estratégicas
- Recompensas frequentes
- Risco vs Recompensa
- Curiosidade
- "Só mais uma run"

Nunca deixar o jogador muitos minutos sem receber alguma recompensa.

---

# Gameplay Loop Principal

```text
Escolher Pokémon Inicial

↓

Entrar na Dungeon

↓

Explorar

↓

Encontrar Pokémon Selvagem

↓

Lutar

↓

Capturar (opcional)

↓

Receber recompensas

↓

Subir de nível

↓

Encontrar itens

↓

Derrotar 3 Pokémon

↓

Centro Pokémon

↓

Escolher recompensa

↓

Próximo andar

↓

Dificuldade aumenta

↓

Repetir infinitamente
```

---

# Escolha Inicial

O jogador SEMPRE escolhe seu Pokémon inicial.

Exemplo:

- Charmander
- Squirtle
- Bulbasaur
- Pikachu

Cada um possui:

- HP diferente
- ataques diferentes
- vantagens de tipo diferentes

O objetivo é gerar identificação logo no início da partida.

---

# Captura

A captura deve ser divertida.

Nunca frustrante.

## Primeiros andares

Pokémons comuns devem ser relativamente fáceis de capturar.

Exemplo:

Pidgey com HP baixo

Chance:

80% ~ 95%

Nunca obrigar o jogador a gastar diversas Pokébolas em um Pokémon extremamente comum.

---

## Chance baseada em

- HP restante
- raridade
- tipo de Pokébola

Nunca apenas sorte pura.

---

# Time

Máximo:

3 Pokémons.

Quando estiver cheio:

Permitir substituir um Pokémon imediatamente.

---

# Progressão

Toda batalha deve recompensar.

Nunca apenas:

"Você venceu."

Sempre oferecer algo.

Exemplos:

- EXP
- moedas
- item
- Pokébola
- cura pequena
- novo ataque
- evolução futura

O jogador deve sentir progresso constante.

---

# Sistema de EXP

Os primeiros níveis devem subir rapidamente.

Exemplo:

Nível 5

↓

6

↓

7

↓

8

↓

9

Depois a progressão desacelera.

Isso mantém o início divertido.

---

# Centro Pokémon

Após derrotar 3 Pokémon:

Gerar automaticamente um Centro Pokémon.

Entrar no Centro realiza:

- cura completa
- restauração de PP (caso exista futuramente)
- mensagem de descanso

Exemplo:

"Todos os seus Pokémon foram completamente curados!"

O Centro representa um momento de alívio.

---

# Escolha de Recompensa

Após sair do Centro Pokémon:

O jogador escolhe UM bônus.

Exemplos:

+1 Pokébola

+1 Poção

+10 HP máximo

Novo ataque

Curar novamente

Aumentar chance de captura no próximo andar

Toda recompensa deve gerar uma decisão.

---

# Andares

Cada novo andar deve aumentar lentamente a dificuldade.

Aumentar:

- HP inimigo
- dano
- variedade
- IA
- tamanho da dungeon

Nunca aumentar apenas números gigantes.

A dificuldade deve parecer justa.

---

# Pokémon Selvagens

Dividir por raridade.

Comum

- Pidgey
- Rattata
- Caterpie

Incomum

- Zubat
- Geodude
- Bellsprout

Raro

- Growlithe
- Abra
- Scyther

Muito raro

- Dratini
- Lapras

O jogador deve ficar feliz ao encontrar um Pokémon raro.

---

# Sistema de Loot

Após cada batalha.

Exemplo:

70%

Poção

20%

Pokébola

8%

Super Poção

2%

Item raro

Nunca deixar muitas batalhas sem recompensa.

---

# Objetivos do Andar

Cada andar pode possuir pequenos desafios.

Exemplo:

☐ Derrotar 3 Pokémon

☐ Capturar 1 Pokémon

☐ Encontrar um baú

☐ Chegar na saída

Ao completar:

Receber recompensa bônus.

---

# Eventos Aleatórios

Ocasionalmente gerar eventos.

Exemplos:

Baú Misterioso

Mercador

Pokémon raro

Fonte de Cura

Treinador perdido

Evento especial

Esses eventos quebram a repetição.

---

# Bosses

A cada 5 andares:

Gerar um Boss.

Derrotar Boss concede:

- recompensa rara
- item especial
- Pokémon raro
- upgrade permanente

---

# Progressão Permanente

Mesmo perdendo uma run.

O jogador pode desbloquear:

Novos Pokémon iniciais

Novos itens

Novos tipos de Pokébola

Novos mapas

Novas skins

Novos desafios

Sempre existir sensação de progresso.

---

# Rejogabilidade

Cada run deve ser diferente.

Alterar:

- mapa
- salas
- Pokémon
- loot
- eventos
- recompensas

Nunca gerar duas runs idênticas.

---

# Ritmo

O jogador nunca deve ficar muito tempo sem receber algo positivo.

Idealmente:

A cada poucos minutos acontecer pelo menos um dos eventos:

- subir de nível
- capturar Pokémon
- encontrar item
- completar missão
- desbloquear algo
- descobrir sala nova

---

# Balanceamento

Evitar extremos.

Nunca:

- capturas impossíveis
- RNG injusto
- inimigos invencíveis
- escassez exagerada

Sempre permitir que boas decisões superem a sorte.

---

# Princípios de Design

Sempre que adicionar um novo sistema perguntar:

Isso cria uma decisão interessante?

Isso aumenta a diversão?

Isso aumenta a estratégia?

Isso aumenta a rejogabilidade?

Se a resposta for "não", reavaliar a necessidade do sistema.

---

# Checklist Contínuo

Após cada alteração verificar:

## Gameplay

- O jogo continua divertido?
- Existe sensação de progresso?
- O jogador recebe recompensas regularmente?
- Existe variedade suficiente?

---

## Balanceamento

- Capturas parecem justas?
- Dificuldade cresce de forma gradual?
- Recursos estão equilibrados?
- Nenhum Pokémon está excessivamente forte?

---

## Dungeon

- Sempre existe caminho até a saída?
- O Centro Pokémon aparece corretamente?
- O próximo andar é gerado?

---

## Interface

- Informações importantes são visíveis?
- O jogador entende seus objetivos?
- O HUD permanece limpo?

---

## Código

- Projeto compila sem erros.
- Não existem regressões.
- Não existem sistemas duplicados.
- As estruturas de dados continuam sendo utilizadas corretamente.
- O código permanece modular e fácil de manter.

---

# Objetivo Final

Quando o jogo estiver completo, o jogador deverá experimentar um ciclo contínuo de:

Explorar → Lutar → Capturar → Evoluir → Escolher → Arriscar → Descansar → Recomeçar.

Cada decisão deve importar.

Cada recompensa deve gerar satisfação.

Cada derrota deve ensinar algo.

Cada nova run deve parecer uma nova aventura.

O jogador deve terminar uma partida pensando:

**"Só mais uma run..."**
