#ifdef GL_ES
    precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform float u_input0;
uniform float u_input1;
uniform float u_input2;

vec3 rgb2hsv(vec3 c)
{
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

vec3 hsv2rgb(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

void main() {
  vec4 c = v_color * texture2D(u_texture, v_texCoords);
  float grey = (c.r + c.g + c.b) / 3.0;
  if (grey == 0.0 || grey == 1.0) {
    gl_FragColor = c;
  } else {
    vec3 c2 = hsv2rgb(vec3(u_input0, u_input1, u_input2));
    c2 = vec3(c2.r * grey, c2.g * grey, c2.b * grey);
    gl_FragColor = vec4(c2.r, c2.g, c2.b, c.a);
  }
}