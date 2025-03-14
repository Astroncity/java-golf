#ifdef GL_ES
precision highp float;
#endif// Samplers
varying vec2 vUV;
uniform sampler2D textureSampler; // Parameters
uniform vec2 curvature;

vec2 curveRemapUV(vec2 uv)
{
    // as we near the edge of our screen apply greater distortion using a cubic function    uv = uv * 2.0–1.0;
    vec2 offset = abs(uv.yx) / vec2(curvature.x, curvature.y);
    uv = uv + uv * offset * offset;
    uv = uv * 0.5 + 0.5;
    return uv;
}void main(void)
{
    vec2 remappedUV = curveRemapUV(vec2(vUV.x, vUV.y));
    vec4 baseColor = texture2D(textureSampler, remappedUV);
    if (remappedUV.x < 0.0 || remappedUV.y < 0.0 || remappedUV.x > 1.0 || remappedUV.y > 1.0) {
        gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
    } else {
        gl_FragColor = baseColor;
    }
}
