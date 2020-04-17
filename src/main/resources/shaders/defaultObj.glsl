#type vertex
#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTexCoords;

out vec3 fPos;
out vec2 fTexCoords;

uniform mat4 uView;
uniform mat4 uProjection;
uniform mat4 uModel;

void main()
{
    fPos = aPos;
    fTexCoords = aTexCoords;

    gl_Position = uProjection * uView * uModel * vec4(aPos, 1.0);
}

#type fragment
#version 330 core
//uniform float uAspect;

out vec4 color;

in vec3 fPos;
in vec2 fTexCoords;

void main()
{
    color = vec4(1, 1, 1, 1);
}