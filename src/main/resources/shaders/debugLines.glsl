#type vertex
#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aColor;

out vec3 fColor;

uniform mat4 uView;
uniform mat4 uProjection;
uniform float uAspect;

void main()
{
    fColor = aColor;

    gl_Position = uProjection * uView * vec4(aPos, 1);
//    gl_Position /= gl_Position.w;

//    vec4 currentProjected = uProjection * uView * vec4(aPos, 1);
//    vec2 currentScreen = currentProjected.xy / currentProjected.w;
//    currentScreen.x *= uAspect;
//
//    vec3 normal = aNormal;
//    normal *= stroke / 2.0;
//    normal.x /= uAspect;
//
//    vec4 offset = vec4(normal, 1.0);
//    gl_Position = currentProjected + offset;
}

#type fragment
#version 330 core
in vec3 fColor;

out vec4 color;

void main()
{
    color = vec4(fColor, 1);
}