#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec2 texCoord1;
in vec4 normal;

out vec4 fragColor;

void main() {
    vec4 color         = texture(Sampler0, texCoord0) * vertexColor * ColorModulator;
    vec4 emissiveColor = texture(Sampler0, texCoord0); // USED FOR ARMOR TRIMS

    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);

    if (color.a < 0.1) {discard;}

    float opacity = ceil(color.a * 255);
    if(opacity == 254) // im guessing this is how it can choose which trims glow and which dont, if i set the opacity to 254 it glows?
     {
        fragColor = linear_fog(emissiveColor, vertexDistance, FogStart, FogEnd, FogColor);
     }
}

// Credits:
// This shader is a modified version of a shader created by ManuelXXVI.
// Credits for the emissive trims go to him, and thank you for letting me use your shader!
// Original Resource Pack by ManuelXXVI: https://www.planetminecraft.com/texture-pack/xxvi-s-shiny-armor-trims/