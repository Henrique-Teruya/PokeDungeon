import os
from PIL import Image, ImageDraw

width = 426
height = 80
img = Image.new("RGBA", (width, height), (0, 0, 0, 0))
draw = ImageDraw.Draw(img)

# Colors inspired by Pokemon FireRed / Retro RPGs
outer_border = (40, 40, 40, 255)       # Dark gray/almost black
inner_border_1 = (168, 184, 184, 255)  # Light gray-blue border
inner_border_2 = (104, 136, 152, 255)  # Darker gray-blue border
bg_color = (248, 248, 248, 255)        # Very light interior
highlight = (255, 255, 255, 255)
shadow = (216, 216, 216, 255)

# Outer shape (rounded corners pixel art style)
draw.rectangle([2, 0, width-3, height-1], fill=outer_border)
draw.rectangle([0, 2, width-1, height-3], fill=outer_border)
draw.point([1, 1], fill=outer_border)
draw.point([width-2, 1], fill=outer_border)
draw.point([1, height-2], fill=outer_border)
draw.point([width-2, height-2], fill=outer_border)

# Inner border 1
draw.rectangle([3, 1, width-4, height-2], fill=inner_border_1)
draw.rectangle([1, 3, width-2, height-4], fill=inner_border_1)
draw.point([2, 2], fill=inner_border_1)
draw.point([width-3, 2], fill=inner_border_1)
draw.point([2, height-3], fill=inner_border_1)
draw.point([width-3, height-3], fill=inner_border_1)

# Inner border 2
draw.rectangle([4, 2, width-5, height-3], fill=inner_border_2)
draw.rectangle([2, 4, width-3, height-5], fill=inner_border_2)
draw.point([3, 3], fill=inner_border_2)
draw.point([width-4, 3], fill=inner_border_2)
draw.point([3, height-4], fill=inner_border_2)
draw.point([width-4, height-4], fill=inner_border_2)

# Background
draw.rectangle([5, 3, width-6, height-4], fill=bg_color)
draw.rectangle([3, 5, width-4, height-6], fill=bg_color)
draw.point([4, 4], fill=bg_color)
draw.point([width-5, 4], fill=bg_color)
draw.point([4, height-5], fill=bg_color)
draw.point([width-5, height-5], fill=bg_color)

# Inner Shadow & Highlight
draw.line([5, height-4, width-6, height-4], fill=shadow, width=1)
draw.line([width-4, 5, width-4, height-6], fill=shadow, width=1)
draw.point([width-5, height-5], fill=shadow)

img.save('/Users/henrique/pokedungeon/assets/ui/barratexto.PNG')
print("Image generated successfully at /Users/henrique/pokedungeon/assets/ui/barratexto.PNG")
