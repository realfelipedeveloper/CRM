{{- define "crm.nome" -}}crm-aurea{{- end -}}
{{- define "crm.labels" -}}
app.kubernetes.io/name: {{ include "crm.nome" . }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end -}}

