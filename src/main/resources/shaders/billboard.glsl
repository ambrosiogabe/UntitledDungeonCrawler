#type vertex
#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec2 aTexCoords;
layout (location = 2) in vec3 aNormal;
layout (location = 3) in vec2 aSquareVerts;

out vec2 fTexCoords;

uniform mat4 uView;
uniform mat4 uProjection;
uniform mat4 uModel;
uniform vec2 uSize;

void main()
{
    fTexCoords = aTexCoords;

    mat4 modelView = uView * uModel;

    // First colunm.
    modelView[0][0] = 1.0;
    modelView[0][1] = 0.0;
    modelView[0][2] = 0.0;

//    // Second colunm.
//    modelView[1][0] = 0.0;
//    modelView[1][1] = 1.0;
//    modelView[1][2] = 0.0;

    // Thrid colunm.
    modelView[2][0] = 0.0;
    modelView[2][1] = 0.0;
    modelView[2][2] = 1.0;

    gl_Position = uProjection * modelView * vec4(aPos, 1.0);
}

#type fragment
#version 330 core
uniform sampler2D uTexture;

in vec2 fTexCoords;

out vec4 color;

void main()
{
    color = texture(uTexture, fTexCoords);
}