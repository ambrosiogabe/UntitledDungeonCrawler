#type vertex
#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTexCoords;
layout (location = 2) in vec3 aNormal;

out vec3 fPos;
out vec2 fTexCoords;

out vec3 fFragPos;
out vec3 fNormal;
out vec3 fLightDir;

uniform mat4 uView;
uniform mat4 uProjection;
uniform mat4 uModel;

uniform vec3 uLightPos;

void main()
{
    fPos = aPos;
    fTexCoords = aTexCoords;

    vec4 worldPos = uModel * vec4(aPos, 1.0);
    gl_Position = uProjection * uView * worldPos;
    fFragPos = vec3(uModel * vec4(aPos, 1.0));

    fNormal = (uModel * vec4(aNormal, 0.0)).xyz;
    fLightDir = uLightPos - worldPos.xyz;
}

#type fragment
#version 330 core
uniform float uAspect;

uniform sampler2D uTexture;
uniform float uUseTexture;
uniform vec3 uLightColor;

out vec4 color;

in vec3 fPos;
in vec2 fTexCoords;

in vec3 fFragPos;
in vec3 fNormal;
in vec3 fLightDir;

void main()
{
    if (uUseTexture == 1.0) {
        color = texture(uTexture, fTexCoords);
    } else if (uUseTexture == 0.0) {
        color = vec4(1, 1, 1, 1);
    } else {
        color = vec4(1, 0, 1, 1);
    }

    vec3 unitNormal = normalize(fNormal);
    vec3 unitLightDir = normalize(fLightDir);

    float nDotl = max(dot(unitNormal, unitLightDir), 0.0);
    vec3 diffuse = nDotl * uLightColor;

    vec3 result = diffuse * color.xyz;
    color = vec4(result, 1) * color;
}