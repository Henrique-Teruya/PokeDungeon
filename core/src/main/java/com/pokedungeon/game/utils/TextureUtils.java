package com.pokedungeon.game.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

/**
 * Utilitário para geração de assets temporários (Placeholders).
 * Ajuda a criar blocos de cores para representar sprites antes
 * de termos as artes finais do jogo.
 */
public class TextureUtils {

    /**
     * Cria uma textura de 1x1 pixel com a cor especificada.
     * Ideal para esticar com o SpriteBatch para formar blocos de UI ou placeholders de sprites.
     *
     * @param color Cor desejada
     * @return Texture gerada (deve ser descartada com dispose() quando não for mais usada)
     */
    public static Texture createColorTexture(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
}
