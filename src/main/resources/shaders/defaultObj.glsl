#type vertex
#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTexCoords;
layout (location = 2) in vec3 aNormal;

out vec3 fPos;
out vec2 fTexCoords;

out vec3 fFragPos;
out vec3 fNormal;

uniform mat4 uView;
uniform mat4 uProjection;
uniform mat4 uModel;

void main()
{
    fPos = aPos;
    fTexCoords = aTexCoords;

    gl_Position = uProjection * uView * uModel * vec4(aPos, 1.0);
    fFragPos = vec3(uModel * vec4(aPos, 1.0));
    fNormal = mat3(transpose(inverse(uModel))) * aNormal;
}

#type fragment
#version 330 core
uniform float uAspect;

uniform sampler2D uTexture;
uniform float uUseTexture;
uniform vec3 uLightPos;

out vec4 color;

in vec3 fPos;
in vec2 fTexCoords;

in vec3 fFragPos;
in vec3 fNormal;

void main()
{
    if (uUseTexture == 1.0) {
        color = texture(uTexture, fTexCoords);
    } else if (uUseTexture == 0.0) {
        color = vec4(1, 1, 1, 1);
    } else {
        color = vec4(1, 0, 1, 1);
    }

    vec3 lightColor = vec3(1, 0.95, 0.71);
    float ambientStrength = 0.1;
    vec3 ambient = ambientStrength * vec3(1, 0.95, 0.71);

    vec3 norm = normalize(fNormal);
    vec3 lightDir = normalize(uLightPos - fFragPos);

    float diff = max(dot(norm, lightDir), 0.0);
    vec3 diffuse = diff * lightColor;

    vec3 result = (ambient + diffuse) * vec3(color);
    color = vec4(result, 1);
}